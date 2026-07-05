package com.ran_yehezkel.billcalcandroid.viewModels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran_yehezkel.billcalcandroid.MainActivity
import com.ran_yehezkel.billcalcandroid.model.HttpRequest
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.ui.moveToPage.MoveToImmutableReceipt
import com.ran_yehezkel.billcalcandroid.ui.moveToPage.MoveToReceipt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream

open class HomeViewModel(val repository: ReceiptRepository): ViewModel()
{

    sealed class UiEvent
    {
        object OpenCamera : UiEvent()
        object OpenGallery : UiEvent()
        class MoveToScreen(val route : String) : UiEvent()
        object MoveToImmutableReceiptScreen : UiEvent()
        class ShowToastMsg(val msg : String) :  UiEvent()
    }
    protected val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()
    private val _showLoadingAnimation  = MutableStateFlow<Boolean>(false)
    open val showLoadingAnimation = _showLoadingAnimation.asStateFlow()
    private val _showExitScreenDialog = MutableStateFlow<String?>(null)
    open val showExitScreenDialog = _showExitScreenDialog.asStateFlow()

    private val _showMsgDialog = MutableStateFlow<String?>(null)
    open val showMsgDialog = _showMsgDialog.asStateFlow()

    open val receiptsDetails: StateFlow<List<ReceiptDetailsUi>> =
        repository.getAllReceiptDetails(limit = 5)
            .map { list -> list.map { ReceiptDetailsUi(it) }}
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun moveToImmutableReceiptScreen(id: Int)
    {
        viewModelScope.launch {
            val receipt = repository.getReceipt(id)
            if (receipt == null)
                return@launch
            MoveToImmutableReceipt.create(receipt.totalPrice,receipt.tip,receipt.items)
            _uiEvents.emit(UiEvent.MoveToScreen(MainActivity.Screen.ImmutableReceipt.route))
        }
    }

    fun userClickExitInAlertDialog()
    {
        val navigateTo = _showExitScreenDialog.value
        dismissExitScreenDialog()
        if (_showLoadingAnimation.value)
        {
            _showLoadingAnimation.value = false
            HttpRequest.cancelRequest()
        }
        if (navigateTo != null)
            viewModelScope.launch {
                    _uiEvents.emit(UiEvent.MoveToScreen(navigateTo))
            }
    }

    open fun showExitScreenDialog(route : String)
    {
        _showExitScreenDialog.value = route
    }

    open fun dismissExitScreenDialog()
    {
        _showExitScreenDialog.value = null
    }

    open fun dismissMsgDialog()
    {
        _showMsgDialog.value = null
    }

    fun openCamera()
    {
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.OpenCamera)
        }
    }


    fun openGallery()
    {
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.OpenGallery)
        }
    }

    fun networkRequestFailed(networkError : Boolean)
    {
        val msg = if (networkError)  "שליחת הבקשה לשרת נכשלה" else "ניתוח התמונה נכשל"
        _showLoadingAnimation.value = false
        _showMsgDialog.value = msg
    }

    fun networkRequestSucceed(receiptJson : String,receiptImage : ImageBitmap)
    {
        try
        {
            val json = JSONObject(receiptJson)
            val totalCostInTheBill = json.optDouble("totalAmount")
            val orderItems = analyzeJson(json)
            if (orderItems.isEmpty())
            {
                val msg = "ארעה שגיאה בניתוח הנתונים"
                _showMsgDialog.value = msg
                return
            }
            if (totalCostInTheBill != 0.0) // 0.0 is not exist
            {
                val totalItemsPrice = orderItems.sumOf { it.price }
                if (totalItemsPrice != totalCostInTheBill)
                {
                    val msg = "AI בידקו את ה" + "\nהסכום בקבלה שונה מסכום הפריטים"
                    viewModelScope.launch {
                        _uiEvents.emit(UiEvent.ShowToastMsg(msg))
                    }
                }
            }
            MoveToReceipt.create(totalCostInTheBill,orderItems,receiptImage)
            dismissExitScreenDialog()
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.MoveToScreen(MainActivity.Screen.Receipt.route))
            }
        }
        catch (e : Exception)
        {
            val msg = "ארעה שגיאה בניתוח הנתונים"
            _showMsgDialog.value = msg
        }
        finally
        {
            _showLoadingAnimation.value = false
        }
    }

    fun analyzeJson(json : JSONObject) : List<ItemInReceipt>
    {
        val billItems = mutableListOf<ItemInReceipt>()
        try
        {
            val items = json.optJSONArray("items")
            if (items != null)
            {
                for (i in 0 until items.length())
                {

                    val item = items.getJSONObject(i)

                    val name = item.getString("name")
                    val quantity = item.getInt("quantity")
                    val unitPrice = item.getDouble("unit_price")

                    repeat(quantity) {
                        billItems.add(ItemInReceipt(name = name,price = unitPrice))
                    }
                }
            }

        }
        catch (e: Exception)
        {
            println(e)
        }

        return billItems
    }

    open fun imageIsChosen(image: InputStream?)
    {
        if (image == null) {
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowToastMsg("בחירת התמונה נכשלה"))
            }
            return
        }
        _showLoadingAnimation.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val base64Image = Utils.inputStreamToBase64(image)
            if (base64Image == null) {
                _uiEvents.emit(UiEvent.ShowToastMsg("בחירת התמונה נכשלה"))
                _showLoadingAnimation.value = false
                return@launch
            }
            HttpRequest.sendReceipt(
                base64Image,
                ::networkRequestSucceed,
                { networkRequestFailed(networkError = true) },
                { networkRequestFailed(networkError = false) }
            )
        }
    }

}

