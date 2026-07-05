package com.ran_yehezkel.billcalcandroid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.ran_yehezkel.billcalcandroid.MainActivity
import com.ran_yehezkel.billcalcandroid.model.SplitReceiptDetails
import com.ran_yehezkel.billcalcandroid.ui.Utils
import com.ran_yehezkel.billcalcandroid.ui.screens.HistoryScreenUtils.ReceiptsSection
import com.ran_yehezkel.billcalcandroid.ui.theme.Colors
import com.ran_yehezkel.billcalcandroid.viewModels.HistoryViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.previews.HistoryViewModelPreview


@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview()
{
    val context = LocalContext.current
    val repo = remember {
        Utils.createReceiptRepository(context)
    }

    val viewModel = remember {
        HistoryViewModelPreview(repo)
    }

    HistoryScreenContent(Modifier, viewModel)
}

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel : HistoryViewModel,
    navigateToRoute : (route : String,push : Boolean) -> Unit)
{
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                HistoryViewModel.UiEvent.MoveToImmutableReceiptScreen ->
                    navigateToRoute(MainActivity.Screen.ImmutableReceipt.route, true)
            }
        }
    }
    HistoryScreenContent(modifier,viewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    modifier: Modifier = Modifier, viewModel : HistoryViewModel)
{
    val showCustomDatesPopup by viewModel.showCustomDatesPopup.collectAsState()
    if (showCustomDatesPopup)
        Utils.PopupCustomFilter(modifier,
                          onDismiss = {viewModel.showCustomDatesPopup(false)},
                          onConfirm = { start, end -> viewModel.onCustomDatesSelected(start, end)})
    val splitReceipts by viewModel.splitReceipts.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val customDatesString by viewModel.customDatesString.collectAsState()
    LaunchedEffect(Unit)
    {
        scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffsetLimit
    }
    Scaffold(
        containerColor = Colors.LightGrayBG,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {HistoryScreenUtils.TopBar(
                  scrollBehavior = scrollBehavior,
                 subHeader = customDatesString)}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), bottom = 16.dp)
        ) {
            val dynamicPadding = ((1 - scrollBehavior.state.collapsedFraction) * 48).dp
            Spacer(modifier = Modifier.height(16.dp + dynamicPadding))
            Utils.FiltersRow(Modifier.padding(16.dp), selectedFilter, {viewModel.setFilter(it)})
            if (splitReceipts.isEmpty())
                HistoryScreenUtils.EmptyReceipts()
            else
                ReceiptsSection(splitReceipts,viewModel)
        }
    }
}

object HistoryScreenUtils
{
    @Composable
    fun EmptyReceipts()
    {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "אין קבלות",
                    color = Color.Gray
                )
            }
        }
    }
    @Composable
    fun ReceiptsSection(splitReceipts : List<SplitReceiptDetails>, viewModel : HistoryViewModel)
    {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            items(splitReceipts) { segment ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(segment.monthYear, fontWeight = FontWeight.SemiBold)
                    for (receipt in segment.receipts)
                        ReceiptItem(receipt.totalPrice,receipt.date) { viewModel.onReceiptClicked(receipt.id) }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }



    @Composable
    fun ReceiptItem(totalPrice: String, date: String,onPressed : () -> Unit)
    {
        Card(modifier = Modifier.fillMaxWidth(),
            onClick = onPressed,
            colors = CardDefaults.cardColors(containerColor = Color.White))
        {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween)
            {
                Text(date, fontWeight = FontWeight.Bold)
                Text(totalPrice, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(scrollBehavior: TopAppBarScrollBehavior,subHeader : String)
    {
        val headerFontSize = lerp(42.sp, 30.sp, scrollBehavior.state.collapsedFraction)
        LargeTopAppBar(
            title = {
                Column()
                {
                    Text(
                        "קבלות",
                        fontWeight = FontWeight.Bold,
                        fontSize = headerFontSize
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subHeader,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            },

            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Colors.LightGrayBG,
                scrolledContainerColor = Colors.LightGrayBG
            )
        )
    }
}
