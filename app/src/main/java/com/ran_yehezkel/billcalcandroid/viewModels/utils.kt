package com.ran_yehezkel.billcalcandroid.viewModels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.exifinterface.media.ExifInterface
import com.ran_yehezkel.billcalcandroid.model.DateHelper
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetails
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.model.SplitReceiptDetails
import com.ran_yehezkel.billcalcandroid.model.TimePeriod
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.graphics.scale

class Utils
{
    companion object
    {

        fun getDateRangeForPeriod(period: TimePeriod) : Pair<Int,Int> //return <start YYYYMMDD,end YYYYMMDD>
        {
            val end = DateHelper.now()
            if (period == TimePeriod.ALL)
                return 0 to end

            val calendar = Calendar.getInstance()
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

            val start = DateHelper.toInt(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
            return start to end
        }



        fun splitReceiptsByMonth(receipts: List<ReceiptDetails>): List<SplitReceiptDetails>
        {

            val locale = Locale("he", "IL") //only for month naming
            val formatter = SimpleDateFormat("MMMM yyyy", locale)

            return receipts
                .groupBy { it.time / 100 } // YYYYMM
                .map { (yearMonth, groupedReceipts) ->

                    val year = yearMonth / 100
                    val month = yearMonth % 100

                    val calendar = Calendar.getInstance().apply {
                        set(year, month - 1, 1)
                    }

                    SplitReceiptDetails(
                        monthYear = formatter.format(calendar.time),
                        receipts = groupedReceipts.map { ReceiptDetailsUi(it) }
                    )
                }
        }

        fun base64ToImageBitmap(base64: String): ImageBitmap
        {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return bitmap.asImageBitmap()
        }


        fun inputStreamToBase64(imageStream: InputStream): String?
        {
            try
            {
                val bytes = imageStream.readBytes()
                imageStream.close()

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null

                val exif = ExifInterface(ByteArrayInputStream(bytes))
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val matrix = Matrix()
                when (orientation)
                {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }

                val finalBitmap = if (!matrix.isIdentity)
                {
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                }
                else
                {
                    bitmap
                }

                val maxDim = 1920
                val scaledBitmap = if (finalBitmap.width > maxDim || finalBitmap.height > maxDim) {
                    val scale = maxDim.toFloat() / maxOf(finalBitmap.width, finalBitmap.height)
                    finalBitmap.scale(
                        (finalBitmap.width * scale).toInt(),
                        (finalBitmap.height * scale).toInt()
                    )
                }
                else
                {
                    finalBitmap
                }

                val baos = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageBytes = baos.toByteArray()
                return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                return null
            }
        }
    }
}
