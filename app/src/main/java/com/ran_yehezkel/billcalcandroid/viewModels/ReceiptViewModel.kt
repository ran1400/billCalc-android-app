package com.ran_yehezkel.billcalcandroid.viewModels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.model.Receipt
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.ui.moveToPage.MoveToReceipt
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class ReceiptViewModel(val repository: ReceiptRepository): ViewModel()
{

    sealed class UiEvent
    {
        class ShowMsg(val msg : String) : UiEvent()
        object MoveToHomeScreen : UiEvent()
    }

    private val _receiptItems = MutableStateFlow<List<ItemInReceipt>>(listOf())
    open val receiptItems = _receiptItems.asStateFlow()

    private val  _receiptImage = MutableStateFlow(ImageBitmap(1,1))
    open val receiptImage = _receiptImage.asStateFlow()
    private val _showExitScreenDialog = MutableStateFlow<String?>(null)
    val showExitScreenDialog = _showExitScreenDialog.asStateFlow()
    private val _showAddTipDialog = MutableStateFlow<Boolean>(false)
    val showAddTipDialog = _showAddTipDialog.asStateFlow()

    private val _showFullScreenImage = MutableStateFlow<Boolean>(false)
    val showFullScreenImage = _showFullScreenImage.asStateFlow()
    private val _showAddItemDialog = MutableStateFlow<Boolean>(false)
    val showAddItemDialog = _showAddItemDialog.asStateFlow()

    private val _showEditItemPriceDialog = MutableStateFlow<Int?>(null)
    val showEditItemPriceDialog = _showEditItemPriceDialog.asStateFlow()

    private val _showEditItemNameDialog = MutableStateFlow<Int?>(null)
    val showEditItemNameDialog = _showEditItemNameDialog.asStateFlow()

    private val _totalPrice = MutableStateFlow<Double>(0.0)
    val totalPrice = _totalPrice.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    open fun initPage() : Boolean
    {
        val moveToReceipt = MoveToReceipt.get()
        if (moveToReceipt == null) //data not exist
            return false
        if (moveToReceipt.isRead())
            return true
        val items : List<ItemInReceipt> = moveToReceipt.items
        val totalPrice : Double? = moveToReceipt.totalPrice
        _receiptItems.value = items
        _receiptImage.value = moveToReceipt.receiptImage
        _totalPrice.value = 0.0
        if (totalPrice != null)
        {
            val isValidTotalCost = validateTotalCostInBill(items,totalPrice)
            if (isValidTotalCost == false)
            {
                val msg = "AI בידקו את ה" + "\nהסכום בקבלה שונה מסכום כל הפריטים"
                viewModelScope.launch {
                    _uiEvents.emit(UiEvent.ShowMsg(msg))
                }
            }
        }
        return true
    }

    fun showFullScreenImage()
    {
        _showFullScreenImage.value = true
    }

    fun exitFromFullScreenImage()
    {
        _showFullScreenImage.value = false
    }


    fun validateTotalCostInBill(orderItems: List<ItemInReceipt> ,totalCostInTheBill : Double ) : Boolean
    {
        var allItemTotalCost = 0.0
        for (orderItem in orderItems)
            allItemTotalCost += orderItem.price
        return allItemTotalCost == totalCostInTheBill
    }

    fun dismissExitScreenDialog()
    {
        _showExitScreenDialog.value = null
    }

    fun showExitScreenDialog(route : String)
    {
        _showExitScreenDialog.value = route
    }

    fun itemChecked(index: Int, isChecked: Boolean)
    {
        val newList = _receiptItems.value.toMutableList()
        newList[index] = newList[index].copy(isChecked = isChecked)
        _receiptItems.value = newList
        refreshTotalPrice()
    }

    fun sharedWithPlusBtn(index: Int)
    {
        val newList = _receiptItems.value.toMutableList()
        val prevItem = newList[index]
        newList[index] = newList[index].copy(sharedWith = prevItem.sharedWith + 1)
        _receiptItems.value = newList
        refreshTotalPrice()
    }

    fun sharedWithMinusBtn(index: Int)
    {
        val newList = _receiptItems.value.toMutableList()
        val prevItem = newList[index]
        if (prevItem.sharedWith == 0)
            return
        newList[index] = newList[index].copy(sharedWith = prevItem.sharedWith - 1)
        _receiptItems.value = newList
        refreshTotalPrice()
    }

    fun refreshTotalPrice()
    {
        val itemsList = _receiptItems.value
        _totalPrice.value = itemsList.filter { it.isChecked }
            .sumOf { it.price / (it.sharedWith + 1) }
    }

    fun showAddTipDialog(bool : Boolean)
    {
        _showAddTipDialog.value = bool
    }

    fun showAddItemDialog(bool: Boolean)
    {
        _showAddItemDialog.value = bool
    }


    fun addItem(name: String, price: Double?)
    {
        if (name.isEmpty() || price == null)
        {
            var msg : String
            if (name.isEmpty())
                msg = "לא הוכנס שם פריט"
            else // price == null
                msg = "לא הוכנס מחיר"
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowMsg(msg))
            }
        }
        else
        {
            val newList = _receiptItems.value.toMutableList()
            newList.add(ItemInReceipt(name = name, price = price))
            _receiptItems.value = newList
        }
        _showAddItemDialog.value = false
    }

    fun showEditItemNameDialog(index: Int)
    {
        _showEditItemNameDialog.value = index
    }

    fun showEditItemPriceDialog(index: Int)
    {
        _showEditItemPriceDialog.value = index
    }

    fun dismissEditItemNameDialog()
    {
        _showEditItemNameDialog.value = null
    }

    fun dismissEditItemPriceDialog()
    {
        _showEditItemPriceDialog.value = null
    }

    fun editItemName(index: Int, name: String)
    {
        if (name.isEmpty())
        {
            val msg = "לא הוכנס שם פריט"
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowMsg(msg))
            }
        }
        else
        {
            val newList = _receiptItems.value.toMutableList()
            newList[index] = newList[index].copy(name = name)
            _receiptItems.value = newList
        }
        dismissEditItemNameDialog()
    }

    fun editItemPrice(index: Int, price: String)
    {
        val newPrice = price.toDoubleOrNull()
        if (newPrice == null)
        {
            val msg = "לא הוכנס מחיר"
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowMsg(msg))
            }
        }
        else
        {
            val newList = _receiptItems.value.toMutableList()
            val item = newList[index]
            newList[index] = item.copy(price = newPrice)
            _receiptItems.value = newList
            if (item.isChecked)
                refreshTotalPrice()
        }
        dismissEditItemPriceDialog()
    }

    fun saveReceipt(tip: Int, totalPrice: Double)
    {
        val receipt = Receipt(tip = tip, totalPrice = totalPrice, items = receiptItems.value)
        _showAddTipDialog.value = false
        viewModelScope.launch {
            repository.saveReceipt(receipt)
            _uiEvents.emit(UiEvent.MoveToHomeScreen)
        }
    }

}