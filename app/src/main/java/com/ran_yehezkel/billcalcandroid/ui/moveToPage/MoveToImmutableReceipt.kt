package com.ran_yehezkel.billcalcandroid.ui.moveToPage

import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt

class MoveToImmutableReceipt private constructor(
                             val totalPrice : Double,
                             val tip : Int,
                             val items : List<ItemInReceipt>,
                             private var isRead : Boolean = false)
{
    companion object
    {
        private var lastInstance: MoveToImmutableReceipt? = null

        fun get(): MoveToImmutableReceipt?
        {
            return lastInstance
        }

        fun create(totalCost: Double,tip : Int,items: List<ItemInReceipt>)
        {
            lastInstance = MoveToImmutableReceipt(totalCost,tip,items)
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


