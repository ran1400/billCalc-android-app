package com.ran_yehezkel.billcalcandroid.viewModels.previews

import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.viewModels.ReceiptViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReceiptViewModelPreview(repository: ReceiptRepository) : ReceiptViewModel(repository)
{
    private val _receiptItems = MutableStateFlow<List<ItemInReceipt>>(Dummy.getDummyReceiptItems())
    override val receiptItems = _receiptItems.asStateFlow()

    private val  _receiptImage = MutableStateFlow(Dummy.getDummyImage())
    override val receiptImage = _receiptImage.asStateFlow()


    override fun initPage(): Boolean
    {
        return true
    }

}