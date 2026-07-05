package com.ran_yehezkel.billcalcandroid.viewModels.previews

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import com.ran_yehezkel.billcalcandroid.model.DateHelper
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetails
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.model.ReceiptsStatistics
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Dummy
{
    companion object
    {

        fun getDummyReceiptsStatistics() : ReceiptsStatistics
        {
            val total  = 105.5
            val biggest = ReceiptDetails(DateHelper.now(),105.5,0)
            val average = 105.5
            val smallest = ReceiptDetails(DateHelper.now(),50.7,1)
            return ReceiptsStatistics(total, biggest, average, smallest)
        }

        fun getDummyImage() : ImageBitmap
        {
            val width = 300
            val height = 300

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = Color.rgb(200, 230, 255)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.color = Color.YELLOW
            canvas.drawCircle(60f, 60f, 30f, paint)
            paint.color = Color.rgb(120, 200, 120)
            canvas.drawRect(0f, 200f, width.toFloat(), height.toFloat(), paint)
            paint.color = Color.rgb(120, 120, 120)
            val path = android.graphics.Path().apply {
                moveTo(120f, 200f)
                lineTo(180f, 120f)
                lineTo(240f, 200f)
                close()
            }
            canvas.drawPath(path, paint)

            return bitmap.asImageBitmap()
        }

        fun getDummyReceiptItems() : List<ItemInReceipt>
        {
            return listOf(
                ItemInReceipt(name = "פטי קסטל", price = 258.0),
                ItemInReceipt(name ="כ.שרדונה", price = 52.0),
                ItemInReceipt(name = "פרלה 750", price = 28.0),
                ItemInReceipt(name = "קולה זירו", price = 14.0),
                ItemInReceipt(name = "קרפצ'יו", price = 89.0),
                ItemInReceipt(name = "מנת לחם",price =  39.0),
                ItemInReceipt(name = "פטי קסטל", price = 258.0),
                ItemInReceipt(name ="כ.שרדונה", price = 52.0),
                ItemInReceipt(name = "פרלה 750", price = 28.0),
                ItemInReceipt(name = "קולה זירו", price = 14.0),
                ItemInReceipt(name = "קרפצ'יו", price = 89.0),
                ItemInReceipt(name = "מנת לחם",price =  39.0),
            )
        }

        fun getDummyReceiptDetailsUi() : List<ReceiptDetailsUi>
        {
            val dummyReceiptsDetails = getDummyReceiptsDetails()
            return dummyReceiptsDetails.map { ReceiptDetailsUi(it) }
        }

        fun getDummyReceiptsDetails() : List<ReceiptDetails>
        {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            val dates = listOf(
                "11.12.2025",
                "20.12.2025",
                "15.01.2026",
                "20.02.2026",
                "13.03.2026"
            )

            val toInts = dates.map {
                val c = Calendar.getInstance().apply { time = sdf.parse(it)!! }
                c.get(Calendar.YEAR) * 10000 +
                        (c.get(Calendar.MONTH) + 1) * 100 +
                        c.get(Calendar.DAY_OF_MONTH)
            }

            return listOf(
                ReceiptDetails(toInts[0], 225.47, 0),
                ReceiptDetails(toInts[1], 35.10, 1),
                ReceiptDetails(toInts[2], 112.90, 2),
                ReceiptDetails(toInts[3], 315.47, 3),
                ReceiptDetails(toInts[4], 72.10, 4)
            )
        }

    }
}