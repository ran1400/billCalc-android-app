package com.ran_yehezkel.billcalcandroid.model.roomDataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReceiptEntity::class, ItemEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun receiptDao(): ReceiptDao
}