package com.ran_yehezkel.billcalcandroid.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ran_yehezkel.billcalcandroid.MainActivity
import com.ran_yehezkel.billcalcandroid.model.ItemInReceipt
import com.ran_yehezkel.billcalcandroid.ui.Utils
import com.ran_yehezkel.billcalcandroid.ui.dialogs.AddItemDialog
import com.ran_yehezkel.billcalcandroid.ui.dialogs.AddTipDialog
import com.ran_yehezkel.billcalcandroid.ui.dialogs.EnterValueDialog
import com.ran_yehezkel.billcalcandroid.ui.dialogs.ExitScreenDialog
import com.ran_yehezkel.billcalcandroid.viewModels.ReceiptViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.previews.ReceiptViewModelPreview


@Preview(showBackground = true)
@Composable
fun ReceiptScreenPreview()
{
    val context = LocalContext.current
    val repo = remember {
        Utils.createReceiptRepository(context)
    }

    val viewModel = remember {
        ReceiptViewModelPreview(repo)
    }

    val modifier = Modifier.fillMaxSize().padding(bottom = 36.dp,top = 16.dp)
    ReceiptScreenContent(modifier,viewModel)
}

@Composable
fun ReceiptScreen(modifier: Modifier,
                  navigateToRoute : (route : String,push : Boolean) -> Unit,
                  viewModel: ReceiptViewModel
)
{
    LaunchedEffect(Unit)
    {
        if (viewModel.initPage() == false) //data removed
            navigateToRoute(MainActivity.Screen.Home.route,false)
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when(event)
            {
                is ReceiptViewModel.UiEvent.MoveToHomeScreen ->
                    navigateToRoute(MainActivity.Screen.Home.route,false)
                is ReceiptViewModel.UiEvent.ShowMsg ->
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
    val showExitScreenDialog by viewModel.showExitScreenDialog.collectAsState()
    if (showExitScreenDialog != null)
        ExitScreenDialog(
            onConfirm = {
                viewModel.dismissExitScreenDialog()
                navigateToRoute(showExitScreenDialog!!,false)
            },
            onDismiss = { viewModel.dismissExitScreenDialog() }
        )
    ReceiptScreenContent(modifier,viewModel)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReceiptScreenContent(modifier: Modifier,viewModel: ReceiptViewModel)
{
    val receiptItems by viewModel.receiptItems.collectAsState()
    val showAddTipDialog by viewModel.showAddTipDialog.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val showFullScreenImage by viewModel.showFullScreenImage.collectAsState()
    BackHandler{
        if (showFullScreenImage)
            viewModel.exitFromFullScreenImage()
        else
            viewModel.showExitScreenDialog(MainActivity.Screen.Home.route)
    }
    if (showAddTipDialog)
        AddTipDialog(
            baseAmount = totalPrice,
            onSave = {tip,totalPrice -> viewModel.saveReceipt(tip,totalPrice)},
            onDismiss = {viewModel.showAddTipDialog(false)})
    val showAddItemDialog by viewModel.showAddItemDialog.collectAsState()
    if (showAddItemDialog)
        AddItemDialog(
            onDismiss = {viewModel.showAddItemDialog(false)},
            onConfirm = {name,price -> viewModel.addItem(name,price)})
    val showEditItemNameDialog by viewModel.showEditItemNameDialog.collectAsState()
    if (showEditItemNameDialog != null)
        EnterValueDialog(
            header = "הכנס שם פריט",
            inputText = receiptItems[showEditItemNameDialog!!].name,
            onDismiss = viewModel::dismissEditItemNameDialog,
            onConfirm = {name -> viewModel.editItemName(showEditItemNameDialog!!,name)}
        )
    val showEditItemPriceDialog by viewModel.showEditItemPriceDialog.collectAsState()
    if (showEditItemPriceDialog != null)
        EnterValueDialog(
            header = "הכנס מחיר",
            isNumber = true,
            inputText = receiptItems[showEditItemPriceDialog!!].price.toString(),
            onDismiss = viewModel::dismissEditItemPriceDialog,
            onConfirm = {price -> viewModel.editItemPrice(showEditItemPriceDialog!!,price)}
        )
    if (showFullScreenImage)
    {
        val receiptImage by viewModel.receiptImage.collectAsState()
        Utils.ImageScreen(modifier,receiptImage,viewModel::exitFromFullScreenImage)
    }
    else
        Scaffold()
        {
            Column(modifier = modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 16.dp))
            {
                Header()
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f))
                {
                    itemsIndexed(receiptItems) { index, item -> ReceiptItem(item,index,viewModel) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Footer(viewModel,totalPrice)
            }
        }
}


@Composable
fun Footer(viewModel : ReceiptViewModel,totalPrice : Double)
{
    val receiptImage by viewModel.receiptImage.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
        )
    {
        ButtonsSections(viewModel)
        TotalAmountSection(receiptImage,totalPrice,viewModel::showFullScreenImage)
    }
}

@Composable
fun ButtonsSections(viewModel : ReceiptViewModel)
{
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    )
    {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        )
        {
            Button(
                onClick = { viewModel.showAddItemDialog(true) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            )
            {
                Text("הוסף פריט")
            }

            Button(
                onClick = {viewModel.showAddTipDialog(true)},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("חישוב טיפ")
            }
        }
    }
}

@Composable
fun TotalAmountSection(receiptImage: ImageBitmap, totalPrice: Double,moveToImageScreen: () -> Unit)
{
    val imageSize = 80.dp
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //row part 1
            Box(modifier = Modifier.size(imageSize))

            //row part 2
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\u200E${"%.0f₪".format(totalPrice)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "לא כולל טיפ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // row part 3
            Image(
                bitmap = receiptImage,
                contentDescription = "Receipt Image",
                modifier = Modifier
                    .size(width = imageSize, height = imageSize)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {moveToImageScreen()},
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun Header()
{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 4.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
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
fun ReceiptItem(item: ItemInReceipt, index : Int, viewModel : ReceiptViewModel)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    )
    {
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = {isChecked -> viewModel.itemChecked(index,isChecked)})
            TextButton(
                modifier = Modifier.width(100.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {viewModel.showEditItemNameDialog(index)},
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            {
                Text(
                item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        TextButton(
            modifier = Modifier.widthIn(max = 100.dp),
            onClick = {viewModel.showEditItemPriceDialog(index)},
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        {
            Text("\u200E${"%.1f".format(item.price)}")
        }

        val capsuleShape = RoundedCornerShape(50)

        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = capsuleShape
                )
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.sharedWithMinusBtn(index) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("-", fontSize = 22.sp)
                }

                Text(
                    text = "${item.sharedWith}",
                    fontSize = 16.sp
                )

                IconButton(
                    onClick = { viewModel.sharedWithPlusBtn(index) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("+", fontSize = 22.sp)
                }
            }
        }
    }
}