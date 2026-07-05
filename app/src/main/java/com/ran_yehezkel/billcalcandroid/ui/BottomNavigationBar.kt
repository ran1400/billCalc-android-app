package com.ran_yehezkel.billcalcandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.History


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp



@Composable
fun BottomNavigationBar(
    onHistoryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDataClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(90.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Surface(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(alpha = 0.92f),
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .graphicsLayer {

                    shape = GenericShape { size, _ ->
                        val curveRadius = 45.dp.toPx()
                        val centerX = size.width / 2

                        moveTo(0f, 0f)

                        lineTo(centerX - curveRadius * 1.5f, 0f)

                        cubicTo(
                            centerX - curveRadius, 0f,
                            centerX - curveRadius, curveRadius,
                            centerX, curveRadius
                        )
                        cubicTo(
                            centerX + curveRadius, curveRadius,
                            centerX + curveRadius, 0f,
                            centerX + curveRadius * 1.5f, 0f
                        )

                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    clip = true
                }
        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onHistoryClick) {
                    Icon(Icons.Rounded.History, "History", tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.width(72.dp))
                IconButton(onClick = onDataClick) {
                    Icon(Icons.Default.BarChart ,"Graph", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(y = (-30).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .clickable { onCameraClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = "Camera",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}