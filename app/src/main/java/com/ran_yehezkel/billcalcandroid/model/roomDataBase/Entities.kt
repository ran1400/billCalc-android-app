package com.ran_yehezkel.billcalcandroid.model.roomDataBase

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: Int,
    val tip: Int,
    val totalPrice: Double
)

@Entity(tableName = "receipt_items",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("receiptId")],
)

data class ItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val receiptId: Int,
    val name: String,
    val isChecked: Boolean,
    val price: Double,
    val sharedWith: Int
)

data class ReceiptWithItems(

    @Embedded
    val receipt: ReceiptEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "receiptId"
    )
    val items: List<ItemEntity>
)
