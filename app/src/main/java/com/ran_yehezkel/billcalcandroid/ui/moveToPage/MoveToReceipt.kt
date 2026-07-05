package com.ran_yehezkel.billcalcandroid.ui.moveToPage

import androidx.compose.ui.graphics.ImageBitmap
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt


class MoveToReceipt private constructor(
                    val totalPrice : Double?,
                    val items : List<ItemInReceipt>,
                    val receiptImage : ImageBitmap,
                    private var isRead : Boolean = false)
{
    companion object
    {
        private var lastInstance: MoveToReceipt? = null

        fun get(): MoveToReceipt?
        {
            return lastInstance
        }

        fun create(totalPrice: Double,items: List<ItemInReceipt>,receiptImage : ImageBitmap)
        {
            if (totalPrice == 0.0)
                lastInstance = MoveToReceipt(null,items,receiptImage)
            else
                lastInstance = MoveToReceipt(totalPrice,items,receiptImage)
        }
    }

    fun isRead() : Boolean
    {
        if (isRead == false)
        {
            isRead = true
            return false
        }
        return true
    }
}