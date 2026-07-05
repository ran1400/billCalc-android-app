package com.ran_yehezkel.billcalcandroid.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview


@Preview
@Composable
fun EnterValueDialogPreview()
{
    EnterValueDialog(
        onConfirm = {},
        onDismiss = {},
        header = "הכנס טקסט",
        inputText = "",
        isNumber = false)
}

@Composable
fun EnterValueDialog(
    inputText : String,
    isNumber : Boolean = false,
    header : String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
)
{
    var text by remember { mutableStateOf(inputText) }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                header,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },

        text = {
            if (isNumber)
            {
                OutlinedTextField(
                    value = text,
                    onValueChange = { input -> text = input},
                    label = { Text("הכנס מחיר") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        textDirection = TextDirection.Ltr
                    )
                )
            }
            else
            {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
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
                    onClick = { onConfirm(text) }
                ) {
                    Text("אישור")
                }
            }
        }
    )

}