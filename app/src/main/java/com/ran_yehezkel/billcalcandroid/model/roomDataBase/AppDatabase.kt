package com.ran_yehezkel.billcalcandroid.model.roomDataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ReceiptEntity::class, ItemEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun receiptDao(): ReceiptDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Rename column and update values to reflect "total participants" instead of "shared with others"
                db.execSQL("ALTER TABLE receipt_items RENAME COLUMN sharedWith TO totalParticipants")
                db.execSQL("UPDATE receipt_items SET totalParticipants = totalParticipants + 1")
            }
        }
    }
}