package com.example.calendy

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.ui.theme.CalendyTheme
import com.example.calendy.view.messageview.MessagePage
import com.example.calendy.view.editplanview.EditPlanPage
import com.example.calendy.view.editplanview.EditPlanViewModel
import com.example.calendy.view.messagepage.MessagePage
import com.example.calendy.view.messagepage.MessagePageViewModel
import com.example.calendy.view.monthlyview.MonthlyPageKT
import com.example.calendy.view.settingview.SettingPage
import com.example.calendy.view.todolistview.ToDoListPage
import com.example.calendy.view.todolistview.TodoListViewModel
import com.example.calendy.view.weeklyview.WeeklyPage
import com.example.calendy.view.weeklyview.WeeklyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView()
                }
            }
        }
    }
}


@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNavigation(navController = navController) }) {
        Box(Modifier.padding(it)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Week,
        BottomNavItem.Month,
        BottomNavItem.Todo,
        BottomNavItem.AiManager,
        BottomNavItem.Setting
    )

    NavigationBar(
        containerColor = Color.White, contentColor = Color(0xFF3F414E)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(icon = {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = stringResource(id = item.title),
                    modifier = Modifier
                        .width(26.dp)
                        .height(26.dp)
                )
            },
                              label = { Text(stringResource(id = item.title), fontSize = 9.sp) },
                              selected = currentRoute==item.screenRoute,
                              colors = NavigationBarItemDefaults.colors(
                                  selectedIconColor = Color.Cyan,
                                  unselectedIconColor = Color.Gray,
                                  selectedTextColor = Color.Black,
                                  unselectedTextColor = Color.Black
                              ),
                              alwaysShowLabel = true,
                              onClick = {
                                  navController.navigate(item.screenRoute) {
                                      navController.graph.startDestinationRoute?.let {
                                          popUpTo(it) { saveState = true }
                                      }
                                      launchSingleTop = true
                                      restoreState = true
                                  }
                              })
        }
    }
}

sealed class BottomNavItem(
    val title: Int, val icon: Int, val screenRoute: String
) {
    object Week : BottomNavItem(
        title = R.string.text_weekly_view,
        icon = R.drawable.outline_format_list_bulleted_24,
        screenRoute = "Week"
    )

    object Month : BottomNavItem(
        title = R.string.text_monthly_view,
        icon = R.drawable.outline_calendar_month_24,
        screenRoute = "Month"
    )

    object Todo : BottomNavItem(
        title = R.string.text_todo_view,
        icon = R.drawable.outline_checklist_rtl_24,
        screenRoute = "Todo"
    )

    object AiManager : BottomNavItem(
        title = R.string.text_manager_view,
        icon = R.drawable.outline_person_outline_24,
        screenRoute = "AiManager"
    )

    object Setting : BottomNavItem(
        title = R.string.setting_view,
        icon = R.drawable.outline_app_settings_alt_24,
        screenRoute = "Setting"
    )
}

sealed class DestinationRoute(val route: String) {

    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Helper method to convert a Date to a String
        fun dateToString(date: Date): String {
            return dateFormat.format(date)
        }
    }

    class AddSchedule(date: Date) : DestinationRoute("EditPage/schedule?date=${dateToString(date)}")

    class AddTodo(date: Date) : DestinationRoute("EditPage/todo?date=${dateToString(date)}")

    class EditSchedule(id: Int) : DestinationRoute("EditPage/schedule?id=$id")

    class EditTodo(id: Int) : DestinationRoute("EditPage/todo?id=$id")
}


@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Month.screenRoute) {
        composable(BottomNavItem.Week.screenRoute) {
            val viewModel: WeeklyViewModel = viewModel(factory = AppViewModelProvider.Factory)
            WeeklyPage(viewModel, onNavigateToEditPage = {id: Int?, type: PlanType, date: Date? ->
                val route = if (id==null) {
                    when (type) {
                        PlanType.SCHEDULE -> DestinationRoute.AddSchedule(date= date?: Date()).route
                        PlanType.TODO     -> DestinationRoute.AddTodo(date = date?: Date()).route
                    }
                } else {
                    when (type) {
                        PlanType.SCHEDULE -> DestinationRoute.EditSchedule(id = id).route
                        PlanType.TODO     -> DestinationRoute.EditTodo(id = id).route
                    }
                }
                navController.navigate(route)
            })
        }
        composable(BottomNavItem.Month.screenRoute) {
            for (t in navController.backQueue) {
                Log.d("GUN", t.destination.toString())
            }
            MonthlyPageKT(onNavigateToEditPage = { id: Int?, type: PlanType, date: Date? ->
                val route = if (id==null) {
                    when (type) {
                        PlanType.SCHEDULE -> DestinationRoute.AddSchedule(
                            date = date ?: Date()
                        ).route

                        PlanType.TODO     -> DestinationRoute.AddTodo(date = date ?: Date()).route
                    }
                } else {
                    when (type) {
                        PlanType.SCHEDULE -> DestinationRoute.EditSchedule(id = id).route
                        PlanType.TODO     -> DestinationRoute.EditTodo(id = id).route
                    }
                }

                navController.navigate(route)
            })
        }
        composable(BottomNavItem.Todo.screenRoute) {
            val viewModel : TodoListViewModel  = viewModel(factory = AppViewModelProvider.Factory)
            ToDoListPage(viewModel, onNavigateToEditPage = { id: Int? ,date: Date? ->
                if(id != null){
                    val route = DestinationRoute.EditTodo(id = id).route
                    navController.navigate(route)
                } else {
                    val route = DestinationRoute.AddTodo(date = date?: Date()).route
                    navController.navigate(route)
                }
            } )
        }
        composable(
            route = "${BottomNavItem.AiManager.screenRoute}?query={query}", arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ), deepLinks = listOf(navDeepLink {
                uriPattern = "calendy://query/{query}"
            })
        ) { entry ->
            for (t in navController.backQueue) {
                Log.d("GUN", t.destination.toString())
            }
            val messagePageViewModel: MessagePageViewModel =
                viewModel(factory = AppViewModelProvider.Factory)

            val userQuery = entry.arguments?.getString("query")
            if (userQuery!=null) {
                messagePageViewModel.setUserInputText(userQuery)
                messagePageViewModel.onSendButtonClicked()
            }
            MessagePage(messagePageViewModel)
        }
        composable(BottomNavItem.Setting.screenRoute) {
            SettingPage()
        }
        composable(route = "EditPage/{type}?id={id}&date={date}",
                   arguments = listOf(navArgument("type") {
                       type = NavType.StringType
                   }, navArgument("id") {
                       type = NavType.StringType
                       nullable = true
                       defaultValue = null
                   }, navArgument("date") {
                       type = NavType.StringType
                       nullable = true
                       defaultValue = null
                   })
        ) { entry ->
            Log.d("GUN", "Recompose")
            Log.d("GUN", entry.arguments?.toString() ?: "Empty Argument")

            val dateString = entry.arguments?.getString("date")
            val date = dateString?.let { DestinationRoute.dateFormat.parse(it) }

            val viewModel: EditPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
            viewModel.initialize(
                id = entry.arguments?.getString("id")?.toIntOrNull(),
                type = when (entry.arguments?.getString("type")) {
                    "schedule" -> PlanType.SCHEDULE
                    else       -> PlanType.TODO
                },
                date = date
            )

            EditPlanPage(viewModel, onNavigateBack = { navController.popBackStack() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CalendyTheme {
        MainScreenView()
    }
}