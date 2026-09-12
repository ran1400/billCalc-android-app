package com.ran_yehezkel.billcalcandroid.ui.moveToPage

import androidx.compose.ui.graphics.ImageBitmap
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt


class MoveToReceipt private constructor(
    val totalPrice : Double?,
    val items : List<ItemInReceipt>,
    val receiptImage : ImageBitmap,
    val userUuid: String?,
    val receiptId: String?,
    private var isRead : Boolean = false)
{
    companion object
    {
        private var lastInstance: MoveToReceipt? = null

        fun get(): MoveToReceipt?
        {
            return lastInstance
        }

        fun create(totalPrice: Double, items: List<ItemInReceipt>, receiptImage : ImageBitmap, userUuid: String?, receiptId: String?)
        {
            if (totalPrice == 0.0)
                lastInstance = MoveToReceipt(null, items, receiptImage, userUuid, receiptId)
            else
                lastInstance = MoveToReceipt(totalPrice, items, receiptImage, userUuid, receiptId)
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
