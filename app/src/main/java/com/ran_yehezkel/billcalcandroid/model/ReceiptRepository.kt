package com.ran_yehezkel.billcalcandroid.model

import com.ran_yehezkel.billcalcandroid.model.roomDataBase.ItemEntity
import com.ran_yehezkel.billcalcandroid.model.roomDataBase.ReceiptDao
import com.ran_yehezkel.billcalcandroid.model.roomDataBase.ReceiptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar


class ReceiptRepository(private val receiptDao: ReceiptDao)
{

    suspend fun saveReceipt(receipt: Receipt)
    {
        val calendar = Calendar.getInstance().apply { timeInMillis = receipt.time }
        val timeInt = DateHelper.toInt(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        
        val receiptId = receiptDao.insertReceipt(
            ReceiptEntity(
                time = timeInt,
                tip = receipt.tip,
                totalPrice = receipt.totalPrice
            )
        ).toInt()

        val items = receipt.items.map {
            ItemEntity(
                receiptId = receiptId,
                name = it.name,
                isChecked = it.isChecked,
                price = it.price,
                sharedWith = it.sharedWith
            )
        }

        receiptDao.insertItems(items)
    }

    suspend fun getReceipt(id: Int): Receipt? 
    {
        val result = receiptDao.getReceiptByIndex(id) ?: return null

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, result.receipt.time / 10000)
            set(Calendar.MONTH, (result.receipt.time % 10000) / 100 - 1)
            set(Calendar.DAY_OF_MONTH, result.receipt.time % 100)
        }

        return Receipt(
            time = calendar.timeInMillis,
            tip = result.receipt.tip,
            totalPrice = result.receipt.totalPrice,
            items = result.items.map {
                ItemInReceipt(
                    name = it.name,
                    isChecked = it.isChecked,
                    price = it.price,
                    sharedWith = it.sharedWith
                )
            }
        )
    }

    fun getAllReceiptDetails(limit: Int? = null): Flow<List<ReceiptDetails>>
    {
        if (limit == null)
            return receiptDao.getAllReceiptDetailsFlow()
        else
            return receiptDao.getAllReceiptDetailsFlowLimit(limit)
    }

    fun getReceiptDetailsForPeriod(period: TimePeriod): Flow<List<ReceiptDetails>>
    {
        if (period == TimePeriod.ALL) 
            return getAllReceiptDetails()

        val calendar = Calendar.getInstance()
        // Reset time to start of today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (period) 
        {
            TimePeriod.LAST_WEEK ->
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            TimePeriod.LAST_MONTH ->
                calendar.add(Calendar.MONTH, -1)
            TimePeriod.START_OF_WEEK ->
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            TimePeriod.START_OF_MONTH ->
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            TimePeriod.LAST_6_MONTHS ->
                calendar.add(Calendar.MONTH, -6)
            TimePeriod.LAST_12_MONTHS ->
                calendar.add(Calendar.MONTH, -12)
            else -> {}
        }
        
        val startTimeInt = DateHelper.toInt(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        
        return receiptDao.getReceiptDetailsSince(startTimeInt)
    }

    fun getReceiptDetailsBetween(start: Int, end: Int): Flow<List<ReceiptDetails>>
    {
        return receiptDao.getReceiptDetailsBetween(start, end)
    }

    fun getStatisticsBetween(start: Int, end: Int): Flow<ReceiptsStatistics?>
    {
        return getReceiptDetailsBetween(start, end).map { calculateStatistics(it) }
    }

    fun getStatisticsForPeriod(period: TimePeriod): Flow<ReceiptsStatistics?>
    {
        return getReceiptDetailsForPeriod(period).map { calculateStatistics(it) }
    }


    private fun calculateStatistics(receipts: List<ReceiptDetails>): ReceiptsStatistics?
    {
        if (receipts.isEmpty()) return null
        val total = receipts.sumOf { it.totalPrice }
        val biggest = receipts.maxByOrNull { it.totalPrice } ?: return null
        val smallest = receipts.minByOrNull { it.totalPrice } ?: return null
        val average = total / receipts.size
        return ReceiptsStatistics(total, biggest, average, smallest)
    }
}
