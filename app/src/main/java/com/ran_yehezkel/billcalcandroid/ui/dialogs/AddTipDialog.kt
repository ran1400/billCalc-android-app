package com.ran_yehezkel.billcalcandroid.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun AddTipDialogPreview()
{
    AddTipDialog(baseAmount = 100.0, onDismiss = {}, onSave = {tip,totalCount -> })
}

@Composable
fun AddTipDialog(
    baseAmount: Double,
    onDismiss: () -> Unit,
    onSave: (tip : Int, totalPrice : Double) -> Unit,
) {

    var selectedPercent by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }

    val finalAmount =
        if (selectedPercent == 0) baseAmount
        else baseAmount + (baseAmount * selectedPercent / 100)

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "%.0f₪".format(finalAmount),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedPercent > 0) Color.Red else Color.Black
            )
            Spacer(Modifier.height(16.dp))
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally
            , modifier = Modifier.focusable())
            {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    listOf(10,12,15).forEach { percent ->

                        val isSelected = selectedPercent == percent

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.Red else Color.Blue)
                                .clickable {
                                    text = ""
                                    selectedPercent = percent
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$percent%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        text = if (clean.isEmpty()) "" else "$clean%"
                        selectedPercent = clean.toIntOrNull() ?: 0
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    label = { Text("טיפ מותאם אישית") },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,color = Color.Red),
                )
            }
        },



        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                Button (
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.outlineVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    Text("ביטול")
                }

                Button(
                    onClick = { onSave(selectedPercent,finalAmount) }
                ) {
                    Text("שמור")
                }
            }
        }
    )
}