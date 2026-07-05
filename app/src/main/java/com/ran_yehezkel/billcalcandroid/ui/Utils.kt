package com.ran_yehezkel.billcalcandroid.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.room.Room
import com.ran_yehezkel.billcalcandroid.model.ReceiptRepository
import com.ran_yehezkel.billcalcandroid.model.roomDataBase.AppDatabase
import com.ran_yehezkel.billcalcandroid.model.TimePeriod
import com.ran_yehezkel.billcalcandroid.viewModels.previews.Dummy

class Utils
{

    companion object
    {
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun PopupCustomFilter(modifier: Modifier,onDismiss : () -> Unit,onConfirm : (Long,Long) -> Unit)
        {
            val dateRangePickerState = rememberDateRangePickerState()
            Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(modifier = modifier.fillMaxSize())
                {
                    Column(modifier = Modifier.fillMaxSize())
                    {
                        DateRangePicker(
                            state = dateRangePickerState,
                            modifier = Modifier.weight(1f),
                            title = null,
                            headline = {
                                DateRangePickerDefaults.DateRangePickerHeadline(
                                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                                    displayMode = dateRangePickerState.displayMode,
                                    dateFormatter = remember { DatePickerDefaults.dateFormatter() },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            showModeToggle = false,
                        )
                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            thickness = 4.dp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextButton(onClick = onDismiss) { Text("ביטול", fontSize = 16.sp) }
                            TextButton(
                                onClick = {
                                    onConfirm(dateRangePickerState.selectedStartDateMillis!!, dateRangePickerState.selectedEndDateMillis!!)
                                },
                                enabled = dateRangePickerState.selectedStartDateMillis != null &&
                                        dateRangePickerState.selectedEndDateMillis != null
                            )
                            {
                                Text("אישור", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun FiltersRow(modifier: Modifier,selectedFilter : TimePeriod,onFilterChanged : (TimePeriod) -> Unit)
        {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimePeriod.entries.forEach { filter ->
                    FilterChip(
                        label = filter.title,
                        isSelected = selectedFilter == filter,
                        onClick = {onFilterChanged(filter)}
                    )
                }
            }
        }

        @Composable
        fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit)
        {
            Surface(
                color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }

        @Preview(showBackground = true)
        @Composable
        fun ImageScreenPreview()
        {
                ImageScreen(Modifier, Dummy.getDummyImage(),{})
        }


        @Composable
        fun ImageScreen(modifier: Modifier,image : ImageBitmap,onExit :() -> Unit)
        {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            val transformableState = rememberTransformableState { zoomChange, panChange, _ ->

                val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                scale = newScale

                if (scale > 1f)
                    offset += panChange
                else //back to original place if there is no zoom
                    offset = Offset.Zero

            }

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    bitmap = image,
                    contentDescription = "Full Screen Receipt",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = transformableState)
                )

                IconButton(
                    onClick = onExit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }

        fun createReceiptRepository(context: Context): ReceiptRepository
        {
            val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "receipts_db"
            ).build()

            return ReceiptRepository(
                db.receiptDao(),
            )
        }

    }

}
