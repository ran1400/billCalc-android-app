package com.ran_yehezkel.billcalcandroid.model

import java.util.Calendar

object DateHelper
{
    fun toInt(day: Int, month: Int, year: Int): Int = year * 10000 + month * 100 + day

    fun now(): Int
    {
        val calendar = Calendar.getInstance()
        return toInt(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
    }
}

data class ReceiptsStatistics(val total: Double,
                              val biggest: ReceiptDetails,
                              val average: Double,
                              val smallest : ReceiptDetails)

data class ItemInReceipt(var name: String,
                         var isChecked : Boolean = false,
                         var price: Double,
                         var sharedWith: Int = 0)


data class ReceiptDetails(val time: Int, val totalPrice: Double,val id : Int)

data class ReceiptDetailsUi(val date: String, val totalPrice: String, val id : Int)
{
    constructor(receipt: ReceiptDetails) : this(
        date = formatDate(receipt.time),
        totalPrice = "\u200E${"%.0f₪".format(receipt.totalPrice)}",
        id = receipt.id
    )

    companion object
    {
        fun formatDate(time: Int): String
        {
            val year = time / 10000
            val month = (time % 10000) / 100
            val day = time % 100

            return "%02d.%02d.%d".format(day, month, year)
        }
    }
}

//separate by MM/YYYY
data class SplitReceiptDetails(val monthYear :String, val receipts : List<ReceiptDetailsUi>)

data class Receipt(val time: Long = System.currentTimeMillis(),
                   val tip: Int,
                   val totalPrice: Double,
                   val items: List<ItemInReceipt>)

enum class TimePeriod(val title: String)
{
    ALL("הכל"),
    CUSTOM("מותאם אישית"),
    START_OF_WEEK("מתחילת השבוע"),
    START_OF_MONTH("מתחילת החודש"),
    LAST_WEEK("שבוע אחרון"),
    LAST_MONTH("חודש אחרון"),
    LAST_6_MONTHS("חצי שנה אחרונה"),
    LAST_12_MONTHS("שנה אחרונה")
}
