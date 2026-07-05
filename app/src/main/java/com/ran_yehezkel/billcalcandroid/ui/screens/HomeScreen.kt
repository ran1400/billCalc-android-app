package com.ran_yehezkel.billcalcandroid.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ran_yehezkel.billcalcandroid.MainActivity
import com.ran_yehezkel.billcalcandroid.viewModels.previews.HomeViewModelPreview
import java.io.File
import com.ran_yehezkel.billcalcandroid.model.ReceiptDetailsUi
import com.ran_yehezkel.billcalcandroid.ui.Utils
import com.ran_yehezkel.billcalcandroid.ui.dialogs.ExitScreenDialog
import com.ran_yehezkel.billcalcandroid.ui.dialogs.ShowMsgDialog
import com.ran_yehezkel.billcalcandroid.ui.screens.HomeScreenUtils.ReceiptActionSection
import com.ran_yehezkel.billcalcandroid.ui.screens.HomeScreenUtils.ReceiptsListSection
import com.ran_yehezkel.billcalcandroid.ui.theme.Colors
import com.ran_yehezkel.billcalcandroid.viewModels.HomeViewModel


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview()
{
    val context = LocalContext.current
    val repo = remember {
        Utils.createReceiptRepository(context)
    }

    val viewModel = remember {
        HomeViewModelPreview(repo)
    }

    val modifier = Modifier.padding(bottom = 32.dp)
    HomeScreenContent(modifier,viewModel)
}

fun createPhotoFile(context: Context): File =
    File.createTempFile("photo_", ".jpg", context.cacheDir)

@Composable
fun HomeScreen(modifier: Modifier,
               viewModel: HomeViewModel,
               navigateToRoute : (route : String,push : Boolean) -> Unit,
               )
{
    val context = LocalContext.current
    var photoFilePath by rememberSaveable { mutableStateOf(createPhotoFile(context).absolutePath) }
    val photoFile = remember(photoFilePath) { File(photoFilePath) }
    val photoUri = remember(photoFilePath) {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    }
    // Keep a stable reference so LaunchedEffect(Unit) always launches with the current URI
    val currentPhotoUri by rememberUpdatedState(photoUri)

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success)
            {
                viewModel.imageIsChosen(context.contentResolver.openInputStream(photoUri))
                photoFilePath = createPhotoFile(context).absolutePath
            }
            else
            {
                Log.d("HomeScreen", "Camera returned success=false, photoUri=$photoUri")
                Toast.makeText(context, "צילום נכשל, נסה שוב", Toast.LENGTH_SHORT).show()
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                viewModel.imageIsChosen(context.contentResolver.openInputStream(uri))
            }
        }

    val showExitScreenDialog by viewModel.showExitScreenDialog.collectAsState()
    if (showExitScreenDialog != null)
        ExitScreenDialog(
            onConfirm = viewModel :: userClickExitInAlertDialog,
            onDismiss = viewModel::dismissExitScreenDialog
        )
    val showMsgDialog by viewModel.showMsgDialog.collectAsState()
    if (showMsgDialog != null)
        ShowMsgDialog(
            msg = showMsgDialog!!,
            onDismiss = viewModel::dismissMsgDialog
        )
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when(event)
            {
                HomeViewModel.UiEvent.OpenCamera ->
                    cameraLauncher.launch(currentPhotoUri)
                HomeViewModel.UiEvent.OpenGallery ->
                {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                HomeViewModel.UiEvent.MoveToImmutableReceiptScreen ->
                    navigateToRoute(MainActivity.Screen.ImmutableReceipt.route,true)
                is HomeViewModel.UiEvent.MoveToScreen ->
                    navigateToRoute(event.route,false)
                is HomeViewModel.UiEvent.ShowToastMsg ->
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
    HomeScreenContent(modifier,viewModel)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenContent(modifier: Modifier = Modifier, viewModel: HomeViewModel)
{
    val showLoadingAnimation by viewModel.showLoadingAnimation.collectAsState()
    val receiptsDetails by viewModel.receiptsDetails.collectAsState()

    if (showLoadingAnimation)
        BackHandler{
                viewModel.showExitScreenDialog(MainActivity.Screen.ExitFromTheApp.route)
        }

    Box(modifier = modifier.fillMaxSize())
    {
        Scaffold(containerColor = Colors.LightGrayBG)
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                ReceiptsListSection(
                    Modifier.weight(1f).fillMaxWidth(),
                    receiptsDetails,
                    viewModel
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReceiptActionSection(viewModel)
            }
        }

        if (showLoadingAnimation)
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable(enabled = false) {}
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 5.dp,
                )
            }
        }
    }
}

object HomeScreenUtils
{

    @Composable
    fun ReceiptsListSection(modifier: Modifier, receiptsDetails : List<ReceiptDetailsUi>, viewModel: HomeViewModel)
    {
        Column(modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            if (receiptsDetails.isEmpty())
                EmptyReceipts()
            else
            {
                Text(
                    text = "קבלות אחרונות",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp))
                {
                    items(receiptsDetails) { receipt ->
                        ReceiptItem(receipt) { viewModel.moveToImmutableReceiptScreen(receipt.id) }
                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyReceipts()
    {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "אין קבלות אחרונות",
                color = Color.Gray
            )
        }
    }

    @Composable
    fun ReceiptActionSection(viewModel: HomeViewModel)
    {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "העלאת קבלה חדשה",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionBox(icon = Icons.Default.PhotoLibrary, label = "העלאה מהגלריה",viewModel::openGallery)
                    ActionBox(icon = Icons.Default.PhotoCamera, label = "צילום קבלה",viewModel::openCamera)
                }
            }
        }
    }

    @Composable
    fun ActionBox(icon: ImageVector, label: String,onClick : ()-> Unit)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally)
        {
            Surface(
                color = Colors.DarkGreen,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(100.dp, 80.dp)
            ) {
                IconButton(onClick = onClick)
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp).size(40.dp)
                    )
                }

            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Colors.DarkGreen,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    @Composable
    fun ReceiptItem(receiptDetails: ReceiptDetailsUi,onClick : () -> Unit)
    {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Text(text = receiptDetails.date, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(
                text = receiptDetails.totalPrice,
                color = Color(0xFF4C7D4C),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


