package com.ran_yehezkel.billcalcandroid


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ran_yehezkel.billcalcandroid.ui.BottomNavigationBar
import com.ran_yehezkel.billcalcandroid.ui.screens.HomeScreen
import com.ran_yehezkel.billcalcandroid.ui.screens.HistoryScreen
import com.ran_yehezkel.billcalcandroid.ui.screens.ReceiptScreen
import com.ran_yehezkel.billcalcandroid.ui.screens.ImmutableReceiptScreen
import com.ran_yehezkel.billcalcandroid.viewModels.ReceiptViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ran_yehezkel.billcalcandroid.ui.Utils
import com.ran_yehezkel.billcalcandroid.ui.screens.StatisticsScreen
import com.ran_yehezkel.billcalcandroid.viewModels.HistoryViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.HomeViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.ImmutableReceiptViewModel
import com.ran_yehezkel.billcalcandroid.viewModels.StatisticsViewModel
import androidx.activity.SystemBarStyle
import com.ran_yehezkel.billcalcandroid.ui.theme.Colors


class MainActivity : ComponentActivity()
{

    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val repo = (application as MyApp).receiptRepository
                return HomeViewModel(repo) as T
            }
        }
    }

    private val receiptViewModel: ReceiptViewModel by viewModels{
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val repo = (application as MyApp).receiptRepository
                return ReceiptViewModel(repo) as T
            }
        }
    }

    private val historyViewModel: HistoryViewModel by viewModels{
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val repo = (application as MyApp).receiptRepository
                return HistoryViewModel(repo) as T
            }
        }
    }

    private val statisticsViewModel: StatisticsViewModel by viewModels{
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val repo = (application as MyApp).receiptRepository
                return StatisticsViewModel(repo) as T
            }
        }
    }

    private val immutableReceiptViewModel: ImmutableReceiptViewModel by viewModels{
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                return ImmutableReceiptViewModel() as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Colors.LightGrayBG.toArgb(),
                Colors.LightGrayBG.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    MainApp()
                }
            }
        }
    }

    @Composable
    fun MainApp()
    {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    onHistoryClick = { navigateToRouteFromNavigationBar(navController,Screen.History.route) },
                    onCameraClick = { navigateToRouteFromNavigationBar(navController,Screen.Home.route) },
                    onDataClick = { navigateToRouteFromNavigationBar(navController,Screen.Data.route) },
                )
            }
            ,containerColor = Colors.LightGrayBG
        ) { padding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues = padding)
            )
        }
    }

    sealed class Screen(val route: String)
    {
        object History : Screen("history_screen")
        object Home : Screen("home_screen")
        object Data : Screen("data_screen")
        object Receipt : Screen("receipt_screen")
        object ImmutableReceipt : Screen("immutable_receipt_screen")
        object ExitFromTheApp : Screen("")
    }


    @Composable
    fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier)
    {
        val navigateToRoute : (String,Boolean) -> Unit
                = {route,push -> navigateToRoute(navController,route,push)}
        val popBackStack : () -> Unit = {navController.popBackStack()}
        NavHost(navController = navController, startDestination = Screen.Home.route)
        {
            composable(Screen.History.route) { HistoryScreen(modifier,historyViewModel,navigateToRoute) }
            composable(Screen.Home.route) { HomeScreen(modifier,homeViewModel,navigateToRoute) }
            composable(Screen.Data.route) { StatisticsScreen(modifier,statisticsViewModel,navigateToRoute) }
            composable(Screen.Receipt.route) { ReceiptScreen(modifier,navigateToRoute,receiptViewModel) }
            composable(Screen.ImmutableReceipt.route) { ImmutableReceiptScreen(modifier,popBackStack,immutableReceiptViewModel) }
        }
    }

    fun navigateToRouteFromNavigationBar(navController: NavHostController, route: String)
    {
        if (navController.currentDestination?.route == route)
            return
        if (navController.currentDestination?.route == Screen.Receipt.route)
        {
            receiptViewModel.showExitScreenDialog(route = route)
            return
        }
        else if (navController.currentDestination?.route == Screen.ImmutableReceipt.route)
            navController.popBackStack()
        else if (navController.currentDestination?.route == Screen.Home.route)
        {
            if (homeViewModel.showLoadingAnimation.value)
            {
                homeViewModel.showExitScreenDialog(route = route)
                return
            }
        }
        navigateToRoute(navController, route)
    }

    //push is only for immutable receipt screen
    fun navigateToRoute(navController: NavHostController, route: String, push :Boolean = false)
    {
        if (route == Screen.ExitFromTheApp.route)
        {
            finish()
            return
        }
        if (push)
        {
            navController.navigate(route)
            return
        }
        navController.navigate(route)
        {

            popUpTo(navController.graph.startDestinationId)
            {
                saveState = true
            }

            launchSingleTop = true

            restoreState = true
        }
    }
}
