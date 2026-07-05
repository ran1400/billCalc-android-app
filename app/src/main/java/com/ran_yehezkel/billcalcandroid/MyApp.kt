package com.ran_yehezkel.billcalcandroid

import android.app.Application
import androidx.core.content.edit
import androidx.room.Room
import com.ran_yehezkel.billcalcandroid.model.roomDataBase.AppDatabase
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import java.util.UUID


class MyApp : Application()
{

    companion object
    {
        lateinit var instance: MyApp
            private set
    }
    lateinit var db: AppDatabase
        private set

    lateinit var receiptRepository: ReceiptRepository
        private set

    override fun onCreate()
    {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "receipts_db"
        ).build()

        receiptRepository = ReceiptRepository(db.receiptDao())
    }

    fun getOrCreateUuid(): String
    {
        val prefs = applicationContext.getSharedPreferences("user_prefs", MODE_PRIVATE)
        var uuid = prefs.getString("user_uuid", null)
        if (uuid == null)
        {
            uuid = UUID.randomUUID().toString()
            prefs.edit { putString("user_uuid", uuid) }
        }
        return uuid
    }

}