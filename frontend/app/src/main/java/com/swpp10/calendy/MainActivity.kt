package com.swpp10.calendy

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.swpp10.calendy.BottomNavItem.AiManager
import com.swpp10.calendy.BottomNavItem.Month
import com.swpp10.calendy.BottomNavItem.Todo
import com.swpp10.calendy.BottomNavItem.Week
import com.swpp10.calendy.ScreenRoute.EditPlan
import com.swpp10.calendy.data.maindb.plan.PlanType
import com.swpp10.calendy.ui.theme.CalendyTheme
import com.swpp10.calendy.utils.DateHelper.parseLocalTimeString
import com.swpp10.calendy.utils.DateHelper.toLocalTimeString
import com.swpp10.calendy.utils.getPlanType
import com.swpp10.calendy.view.editplanview.EditPlanPage
import com.swpp10.calendy.view.editplanview.EditPlanViewModel
import com.swpp10.calendy.view.messagepage.MessagePageViewModel
import com.swpp10.calendy.view.messageview.MessagePage
import com.swpp10.calendy.view.monthlyview.MonthlyPageKT
import com.swpp10.calendy.view.voiceAssistance.VoiceAssistancePopup
import com.swpp10.calendy.view.todolistview.ToDoListPage
import com.swpp10.calendy.view.weeklyview.WeeklyPage
import com.swpp10.calendy.view.weeklyview.WeeklyViewModel

import java.util.Date


class MainActivity : ComponentActivity() {
    private var isMainScreenLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { !isMainScreenLoaded }
        setContent {
            CalendyTheme(darkTheme = false, dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(onLoaded = { isMainScreenLoaded = true })
                }
            }
        }
    }
}


@Composable
fun MainScreenView(onLoaded: () -> Unit) {
    val navController = rememberNavController()
    var showBottomNavigation by remember { mutableStateOf(true) }

    val editPlanViewModel: EditPlanViewModel = viewModel(factory = com.swpp10.calendy.AppViewModelProvider.Factory)
    fun navigateToEditPage(id: Int?, type: PlanType, startDate: Date?, endDate: Date?) {
        editPlanViewModel.initialize(id = id, type = type, startDate = startDate, endDate = endDate)
        navController.navigate(EditPlan.screenRoute)
    }

    LaunchedEffect(Unit) {
        onLoaded()
    }
    Scaffold(bottomBar = {
        if (showBottomNavigation) {
            BottomNavigation(
                navController = navController,
            ) { planType: PlanType ->
                navigateToEditPage(
                    id = null, type = planType, startDate = Date(), endDate = null
                )
            }
        }
    }) {
        Box(Modifier.padding(it)) {
            NavigationGraph(
                navController = navController,
                showBottomNavigation = { shouldShow -> showBottomNavigation = shouldShow },
                editPlanViewModel = editPlanViewModel,
                navigateToEditPage = { id, type, startDate, endDate ->
                    navigateToEditPage(id, type, startDate, endDate)
                }
            )
        }
    }
}

fun NavController.navigateToBottom(bottomNav: BottomNavItem) {
    navigate(bottomNav.screenRoute) {
        popUpTo(graph.id) {
            // pop start destination too, so that back button quits app
            inclusive = true
//            saveState = true
        }
        launchSingleTop = true
//        restoreState = true
    }
}

@Composable
fun BottomNavigation(
    navController: NavHostController, navigateToEditPageWhenPlus: (PlanType) -> Unit
) {
    val items = listOf(
        Week,
        Month,
        BottomNavItem.Dummy,
        Todo,
        AiManager,
        //BottomNavItem.Setting
    )
    var selectedNavItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Month) }
    var micOn by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        when (currentRoute) {
            Todo.screenRoute      -> selectedNavItem = Todo
            Week.screenRoute      -> selectedNavItem = Week
            Month.screenRoute     -> selectedNavItem = Month
            AiManager.screenRoute -> selectedNavItem = AiManager
        }
    }

    val context = LocalContext.current
    // This is the launcher for permission request
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("GUN", "MIC PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d("GUN", "MIC PERMISSION DENIED")
        }
    }

    NavigationBar(
        containerColor = Color.White, contentColor = Color(0xFFB2CDFF)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            if (item!=BottomNavItem.Dummy) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon!!),
                            contentDescription = stringResource(id = item.title!!),
                            modifier = Modifier
                                .width(26.dp)
                                .height(26.dp),
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = item.title!!),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    selected = currentRoute==item.screenRoute,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        unselectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color(0xE5EEFF)
                    ),
                    alwaysShowLabel = true,
                    onClick = {
                        selectedNavItem = item
                        navController.navigateToBottom(item)
                    },
                )
            } else {
                FloatingActionButton(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) { // Check permission
                            ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.RECORD_AUDIO
                            )    -> {
                                // Permission OK
                                micOn = true
                            }

                            else -> {
                                // NO Permission
                                // Use launcher to ask for permission
                                launcher.launch(android.Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    containerColor = Color(0xFF80ACFF),
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    shape = CircleShape,
                ) {

                    Icon(imageVector = Icons.Filled.GraphicEq, contentDescription = "add plan")
                }
            }

        }
    }

    if(micOn) {
        VoiceAssistancePopup (
            viewModel = viewModel(factory = com.swpp10.calendy.AppViewModelProvider.Factory),
            onDismissRequest = {
                micOn = false
            }
        )
    }
}

sealed class ScreenRoute(val screenRoute: String) {
    object Week : ScreenRoute("Week")
    object Month : ScreenRoute("Month")
    object Todo : ScreenRoute("Todo")
    object AiManager : ScreenRoute("AiManager")
    object Dummy : ScreenRoute("Dummy")
    object EditPlan : ScreenRoute("EditPlan")
}

sealed class BottomNavItem(val title: Int?, val icon: Int?, route: ScreenRoute) {
    val screenRoute = route.screenRoute

    object Week : BottomNavItem(
        title = R.string.text_weekly_view,
        icon = R.drawable.outline_format_list_bulleted_24,
        route = ScreenRoute.Week
    )

    object Month : BottomNavItem(
        title = R.string.text_monthly_view,
        icon = R.drawable.outline_calendar_month_24,
        route = ScreenRoute.Month
    )

    object Todo : BottomNavItem(
        title = R.string.text_todo_view,
        icon = R.drawable.outline_checklist_rtl_24,
        route = ScreenRoute.Todo
    )

    object AiManager : BottomNavItem(
        title = R.string.text_manager_view,
        icon = R.drawable.outline_person_outline_24,
        route = ScreenRoute.AiManager
    )

    // This is for plus button in middle
    object Dummy : BottomNavItem(
        title = null, icon = null, route = ScreenRoute.Dummy
    )
}


@Composable
fun NavigationGraph(
    navController: NavHostController,
    showBottomNavigation: (Boolean) -> Unit,
    editPlanViewModel: EditPlanViewModel,
    navigateToEditPage: (Int?, PlanType, Date?, Date?) -> Unit
) {
    NavHost(navController = navController, startDestination = Month.screenRoute) {
        // region Bottom Navigation Routes
        composable(Week.screenRoute) {
            showBottomNavigation(true)
            WeeklyPage { id: Int?, type: PlanType, startDate: Date?, endDate: Date? ->
                navigateToEditPage(id, type, startDate, endDate)
            }
        }
        composable(Month.screenRoute) {
            showBottomNavigation(true)
            MonthlyPageKT { id: Int?, type: PlanType, startDate: Date?, endDate: Date? ->
                navigateToEditPage(id, type, startDate, endDate)
            }
        }
        composable(Todo.screenRoute) {
            showBottomNavigation(true)
            ToDoListPage { id: Int?, startDate: Date?, endDate: Date? ->
                navigateToEditPage(id, PlanType.TODO, startDate, endDate)
            }
        }
        composable(AiManager.screenRoute) {
            showBottomNavigation(true)
            MessagePage(
                messagePageViewModel = viewModel(factory = com.swpp10.calendy.AppViewModelProvider.Factory),
                onNavigateToEditPage = {
                    navigateToEditPage(it.id, it.getPlanType(), null, null)
                },
            )
        }
        //endregion
        composable(EditPlan.screenRoute) {
            showBottomNavigation(false)
            EditPlanPage(editPlanViewModel, onNavigateBack = navController::popBackStack)
        }
        composable(
            route = "QueryRoute?query={query}", arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ), deepLinks = listOf(navDeepLink {
                uriPattern = "calendy://query/{query}"
            })
        ) { entry ->
            // QueryRoute do not have a page. It just navigate to AiManager
            val messagePageViewModel: MessagePageViewModel =
                viewModel(factory = com.swpp10.calendy.AppViewModelProvider.Factory)

            val userQuery = entry.arguments?.getString("query")
            if (userQuery!=null) {
                messagePageViewModel.setUserInputText(userQuery)
                messagePageViewModel.onSendButtonClicked()
            }

            // Navigate To ManagerPage
            navController.navigateToBottom(AiManager)
        }
        composable(
            route = "BriefingRoute?time={timeScope}", arguments = listOf(
                navArgument("timeScope") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ), deepLinks = listOf(navDeepLink {
                uriPattern = "calendy://briefing/{timeScope}"
            })
        ) { entry ->
            val messagePageViewModel: MessagePageViewModel =
                viewModel(factory = com.swpp10.calendy.AppViewModelProvider.Factory)

            val timeScope = entry.arguments?.getString("timeScope")
            if (timeScope!=null) {
            }
            // Set ViewModel, and navigate to ManagerPage
        }
    }
}

