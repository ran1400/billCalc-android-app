package com.ran_yehezkel.billcalcandroid.viewModels

import androidx.lifecycle.ViewModel
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.ui.moveToPage.MoveToImmutableReceipt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ImmutableReceiptViewModel : ViewModel()
{
    private val _tip = MutableStateFlow<Int>(0)
    open val tip = _tip.asStateFlow()
    private val _totalPrice = MutableStateFlow<Double>(0.0)
    open val totalPrice = _totalPrice.asStateFlow()

    private val _receiptItems = MutableStateFlow<List<ItemInReceipt>>(emptyList())
    open val receiptItems = _receiptItems.asStateFlow()

    open fun initScreen() : Boolean
    {
        val moveToImmutableReceipt = MoveToImmutableReceipt.get()
        if (moveToImmutableReceipt == null) //data is not exist
            return false
        if (moveToImmutableReceipt.isRead())
            return true
        _totalPrice.value = moveToImmutableReceipt.totalPrice
        _tip.value = moveToImmutableReceipt.tip
        _receiptItems.value = moveToImmutableReceipt.items
        return true
    }

}