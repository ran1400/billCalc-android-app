package com.ran_yehezkel.billcalcandroid.viewModels.previews

import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.viewModels.ImmutableReceiptViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImmutableReceiptViewModelPreview() : ImmutableReceiptViewModel()
{

    private val _tip = MutableStateFlow<Int>(5)
    override val tip = _tip.asStateFlow()
    private val _totalPrice = MutableStateFlow<Double>(1000.0)
    override val totalPrice = _totalPrice.asStateFlow()

    private val _receiptItems = MutableStateFlow<List<ItemInReceipt>>(Dummy.getDummyReceiptItems())
    override val receiptItems = _receiptItems.asStateFlow()

    override fun initScreen() : Boolean
    {
        return true
    }

}
