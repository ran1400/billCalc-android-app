package com.ran_yehezkel.billcalcandroid.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.ran_yehezkel.billcalcandroid.MainActivity
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.model.ReceiptsStatistics
import com.ran_yehezkel.billcalcandroid.ui.Utils
import com.ran_yehezkel.billcalcandroid.ui.theme.Colors
import com.ran_yehezkel.billcalcandroid.viewModels.StatisticsViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.previews.StatisticsViewModelPreview

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview()
{
    val context = LocalContext.current
    val repo = remember {
        Utils.createReceiptRepository(context)
    }

    val viewModel = remember {
        StatisticsViewModelPreview(repo)
    }
    StatisticsScreenContent(Modifier.padding(bottom = 32.dp),viewModel)
}

@Composable
fun StatisticsScreen(
    modifier: Modifier,
    viewModel: StatisticsViewModel,
    navigateToRoute : (route : String,push : Boolean) -> Unit)
{
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                StatisticsViewModel.UiEvent.MoveToImmutableReceiptScreen ->
                    navigateToRoute(MainActivity.Screen.ImmutableReceipt.route, true)
            }
        }
    }
    val showCustomDatesPopup by viewModel.showCustomDatesPopup.collectAsState()
    if (showCustomDatesPopup)
        Utils.PopupCustomFilter(modifier,
            onDismiss = {viewModel.showCustomDatesPopup(false)},
            onConfirm = { start, end -> viewModel.onCustomDatesSelected(start, end)})
    StatisticsScreenContent(modifier,viewModel)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StatisticsScreenContent(modifier: Modifier,viewModel: StatisticsViewModel)
{
    Scaffold(
        containerColor = Colors.LightGrayBG
    )
    {
        val receiptsStatistics by viewModel.receiptsStatistics.collectAsState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 32.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))
            val customDatesString by viewModel.customDatesString.collectAsState()
            if (receiptsStatistics == null)
                BlankHeader(customDatesString)
            else
                Header(receiptsStatistics!!, customDatesString)

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                val selectedFilter by viewModel.selectedFilter.collectAsState()
                Utils.FiltersRow(Modifier.padding(16.dp), selectedFilter, {viewModel.setFilter(it)})
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatisticsSegment(receiptsStatistics,viewModel)
            }
        }
    }
}

@Composable
fun StatisticsSegment(receiptsStatistics : ReceiptsStatistics?,viewModel: StatisticsViewModel)
{
    var maxReceiptPrice : String? = null
    var maxReceiptDate : String? = null
    var averageReceiptPrice : String? = null
    var minReceiptPrice : String? = null
    var minReceiptDate : String? = null
    var maxReceiptOnClick : (() -> Unit)? = null
    var minReceiptOnClick : (() -> Unit)? = null
    if (receiptsStatistics != null)
    {
        val minReceiptDetailsUi = ReceiptDetailsUi(receiptsStatistics.smallest)
        val maxReceiptDetailsUi = ReceiptDetailsUi(receiptsStatistics.biggest)
        maxReceiptPrice = maxReceiptDetailsUi.totalPrice
        maxReceiptDate = maxReceiptDetailsUi.date
        maxReceiptOnClick = { viewModel.onReceiptClicked(maxReceiptDetailsUi.id) }
        minReceiptPrice = minReceiptDetailsUi.totalPrice
        minReceiptDate = minReceiptDetailsUi.date
        minReceiptOnClick = { viewModel.onReceiptClicked(minReceiptDetailsUi.id) }
        averageReceiptPrice = "%.0f₪".format(receiptsStatistics.average)
    }
    SummaryCard("ההוצאה הגדולה ביותר",maxReceiptPrice,maxReceiptDate,maxReceiptOnClick)
    SummaryCard(label = "הוצאה ממוצעת", price = averageReceiptPrice)
    SummaryCard(label = "ההוצאה הקטנה ביותר",minReceiptPrice,minReceiptDate,minReceiptOnClick)
}

@Composable
fun Header(receiptsStatistics: ReceiptsStatistics, date : String)
{
    val totalPrices = "%.0f₪".format(receiptsStatistics.total)
    Text(
        text = "סה\"כ הוצאות",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black
    )

    Text(
        text = totalPrices ,
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF388E3C)
    )

    Text(
        text = date,
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun BlankHeader(date : String)
{
    if (date == "הכל")
    {
        Text(
            text = "אין הוצאות",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        return
    }
    Text(
        text = "אין הוצאות בתאריכים שנבחרו",
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black
    )

    Text(
        text = date,
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun SummaryCard(label: String, price: String? = null, date: String? = null,onClick: (() -> Unit)? = null)
{
    var cardModifier = Modifier.fillMaxWidth()
    if (onClick != null)
        cardModifier = cardModifier.clickable { onClick() }
    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {

            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Column(horizontalAlignment = Alignment.End)
            {
                Text(
                    text = price ?: "---",
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                if (date != null) //date and time
                {
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
            }

        }
    }

}

