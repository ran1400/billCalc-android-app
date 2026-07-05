package com.ran_yehezkel.billcalcandroid.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun ShowMsgDialogPreview()
{
    ShowMsgDialog(msg = "Preview message", onDismiss = {})
}

@Composable
fun ShowMsgDialog(msg: String, onDismiss: () -> Unit)
{
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(msg, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(onClick = onDismiss) {
                    Text("סגור")
                }
            }
        }
    )
}