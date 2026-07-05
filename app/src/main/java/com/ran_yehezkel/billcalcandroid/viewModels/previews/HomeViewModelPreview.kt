package com.ran_yehezkel.billcalcandroid.viewModels.previews

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.ran_yehezkel.billcalcandroid.MainActivity
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.ui.moveToPage.MoveToReceipt
import com.ran_yehezkel.billcalcandroid.viewModels.HomeViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class HomeViewModelPreview(repository: ReceiptRepository) : HomeViewModel(repository)
{

    private val _showLoadingAnimation  = MutableStateFlow<Boolean>(false)
    override val showLoadingAnimation = _showLoadingAnimation.asStateFlow()

    private val _showExitScreenDialog = MutableStateFlow<String?>(null)
    override val showExitScreenDialog = _showExitScreenDialog.asStateFlow()


    private val _receiptsDetails = MutableStateFlow(Dummy.getDummyReceiptDetailsUi())

    override val receiptsDetails: StateFlow<List<ReceiptDetailsUi>> = _receiptsDetails.asStateFlow()

    override fun showExitScreenDialog(route : String)
    {
        _showExitScreenDialog.value = route
    }

    override fun dismissExitScreenDialog()
    {
        _showExitScreenDialog.value = null
    }


    override fun imageIsChosen(image: InputStream?)
    {
        Log.d("HomeScreenViewModel", "imageIsTaken")
        if (image == null)
        {
            val msg = "בחירת התמונה נכשלה"
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowToastMsg(msg))
            }
        }
        val base64Image = Utils.inputStreamToBase64(image!!)
        if (base64Image == null)
        {
            val msg = "בחירת התמונה נכשלה"
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowToastMsg(msg))
            }
        }
        networkRequestMockup(Utils.base64ToImageBitmap(base64Image!!))
    }

    fun networkRequestMockup(receiptImage : ImageBitmap)
    {
        val receiptItemsForPreview = Dummy.getDummyReceiptItems()
        MoveToReceipt.create(0.0,receiptItemsForPreview, receiptImage)
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.MoveToScreen(MainActivity.Screen.Receipt.route))
        }
    }

}