package com.ran_yehezkel.billcalcandroid.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.room.Room
import com.ran_yehezkel.billcalcandroid.R
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

                            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), fontSize = 16.sp) }
                            TextButton(
                                onClick = {
                                    onConfirm(dateRangePickerState.selectedStartDateMillis!!, dateRangePickerState.selectedEndDateMillis!!)
                                },
                                enabled = dateRangePickerState.selectedStartDateMillis != null &&
                                        dateRangePickerState.selectedEndDateMillis != null
                            )
                            {
                                Text(stringResource(R.string.confirm), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun FiltersRow(modifier: Modifier,selectedFilter : TimePeriod,onFilterChanged : (TimePeriod) -> Unit)
        {
            val scrollState = rememberScrollState()
            val filters = listOf(
                TimePeriod.ALL,
                TimePeriod.START_OF_WEEK,
                TimePeriod.START_OF_MONTH,
                TimePeriod.LAST_WEEK,
                TimePeriod.LAST_MONTH,
                TimePeriod.LAST_6_MONTHS,
                TimePeriod.LAST_12_MONTHS,
                TimePeriod.CUSTOM
            )

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState)
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // This spacer aligns the first chip with the scrollbar track (16dp)
                    // 8dp (Spacer) + 8dp (spacedBy) = 16dp
                    Spacer(modifier = Modifier.width(8.dp))
                    filters.forEach { filter ->
                        FilterChip(
                            label = filter.title,
                            isSelected = selectedFilter == filter,
                            onClick = {onFilterChanged(filter)}
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Persistent Scrollbar
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .height(5.dp) // Thicker indicator
                        .background(Color.LightGray.copy(alpha = 0.3f), CircleShape)
                ) {
                    val maxScroll = scrollState.maxValue.toFloat()
                    if (maxScroll > 0) {
                        val thumbWidthFraction = 0.2f
                        val scrollFraction = scrollState.value.toFloat() / maxScroll
                        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(thumbWidthFraction)
                                .fillMaxHeight()
                                .align(Alignment.CenterStart)
                                .graphicsLayer {
                                    // In RTL, translationX moves to the right. 
                                    // CenterStart is on the right, so we need negative translation to move left.
                                    val availableWidth = size.width * 4f
                                    translationX = if (isRtl) -scrollFraction * availableWidth else scrollFraction * availableWidth
                                }
                                .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                        )
                    }
                }
            }
        }

        @Composable
        fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit)
        {
            Surface(
                onClick = onClick,
                color = if (isSelected) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) Color(0xFF2E7D32) else Color.LightGray.copy(alpha = 0.6f)
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    color = if (isSelected) Color(0xFF2E7D32) else Color.Black.copy(alpha = 0.87f),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
