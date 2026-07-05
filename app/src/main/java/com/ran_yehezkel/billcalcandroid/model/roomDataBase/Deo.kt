package com.ran_yehezkel.billcalcandroid.model.roomDataBase

import androidx.room.*
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao
{

    @Insert
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Insert
    suspend fun insertItems(items: List<ItemEntity>)

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptByIndex(id: Int): ReceiptWithItems?

    @Query("""
    SELECT id, time, totalPrice 
    FROM receipts
    ORDER BY id DESC
    LIMIT :limit
    """)

    fun getAllReceiptDetailsFlowLimit(limit: Int): Flow<List<ReceiptDetails>>

    @Query("""
    SELECT id, time, totalPrice 
    FROM receipts
    ORDER BY id DESC
    """)
    fun getAllReceiptDetailsFlow(): Flow<List<ReceiptDetails>>

    @Query("""
    SELECT id, time, totalPrice 
    FROM receipts
    WHERE time >= :startTime
    ORDER BY time DESC
    """)
    fun getReceiptDetailsSince(startTime: Int): Flow<List<ReceiptDetails>>

    @Query("""
    SELECT id, time, totalPrice 
    FROM receipts
    WHERE time >= :startTime AND time <= :endTime
    ORDER BY time DESC
    """)
    fun getReceiptDetailsBetween(startTime: Int, endTime: Int): Flow<List<ReceiptDetails>>
}
