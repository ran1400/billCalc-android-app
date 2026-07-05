package com.ran_yehezkel.billcalcandroid.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.viewModels.ImmutableReceiptViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.previews.ImmutableReceiptViewModelPreview


@Preview(showBackground = true)
@Composable
fun ImmutableReceiptScreenPreview()
{
    val modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 32.dp,top = 16.dp)
    val viewModel : ImmutableReceiptViewModelPreview = viewModel()
    ReceiptViewScreenContent(modifier, viewModel)
}

@Composable
fun ImmutableReceiptScreen(modifier: Modifier,
                           popBackStack : () -> Unit,
                           viewModel : ImmutableReceiptViewModel)
{
    LaunchedEffect(Unit)
    {
        if (viewModel.initScreen() == false) //data removed
            popBackStack()
    }
    ReceiptViewScreenContent(modifier, viewModel)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReceiptViewScreenContent(modifier: Modifier,viewModel : ImmutableReceiptViewModel)
{
    val receiptItems by viewModel.receiptItems.collectAsState()
    val tip by viewModel.tip.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    Scaffold()
    {
        Column(modifier = modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 16.dp))
        {
            ReceiptViewScreenHelpers.Header()
            Spacer(Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f))
            {
                items(receiptItems) { item -> ReceiptViewScreenHelpers.OrderRow(item) }
            }
            Spacer(Modifier.height(16.dp))
            ReceiptViewScreenHelpers.Footer(totalPrice,tip)
        }
    }
}

object ReceiptViewScreenHelpers
{
    @Composable
    fun Header()
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {

            Text(
                text = "פריט",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "חלקתי עם",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }


    @Composable
    fun Footer(totalPrice : Double , tip : Int)
    {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = "%.0f₪".format(totalPrice),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text =  "כולל " + "$tip%" +  " טיפ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }

    @Composable
    fun OrderRow(item: ItemInReceipt)
    {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        )
        {
            Row(
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Checkbox(checked = item.isChecked, onCheckedChange = null,enabled = false)
                Spacer(Modifier.width(8.dp))
                Text(text = item.name,modifier = Modifier.width(100.dp))
                Spacer(Modifier.width(16.dp))
                Text(text = "${item.price}₪")
            }
            Row()
            {
                Text(text = "${item.sharedWith}",fontSize = 16.sp)
                Spacer(Modifier.width(32.dp))
            }

        }
    }
}

