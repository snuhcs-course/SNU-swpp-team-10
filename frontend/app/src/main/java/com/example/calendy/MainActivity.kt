package com.example.calendy

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import com.example.calendy.data.plan.Plan
import com.example.calendy.ui.theme.CalendyTheme
import com.example.calendy.view.editplanview.EditPlanPage
import com.example.calendy.view.monthlyview.MonthlyPage
import com.example.calendy.view.editplanview.EditPlanViewModel
import com.example.calendy.view.monthlyview.MonthlyPageKT


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
        icon = R.drawable.ic_android_black_24dp,
        screenRoute = "Week"
    )

    object Month : BottomNavItem(
        title = R.string.text_monthly_view,
        icon = R.drawable.ic_android_black_24dp,
        screenRoute = "Month"
    )

    object Todo : BottomNavItem(
        title = R.string.text_todo_view,
        icon = R.drawable.ic_android_black_24dp,
        screenRoute = "Todo"
    )

    object AiManager : BottomNavItem(
        title = R.string.text_manager_view,
        icon = R.drawable.ic_android_black_24dp,
        screenRoute = "AiManager"
    )

    object Setting : BottomNavItem(
        title = R.string.setting_view,
        icon = R.drawable.ic_android_black_24dp,
        screenRoute = "Setting"
    )
}

sealed class DestinationRoute(val route: String) {
    object AddSchedule : DestinationRoute("EditPage/schedule")

    object AddTodo : DestinationRoute("EditPage/todo")

    class EditSchedule(id: Int) : DestinationRoute("EditPage/schedule?id=$id")

    class EditTodo(id: Int) : DestinationRoute("EditPage/todo?id=$id")
}


@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Week.screenRoute) {
        composable(BottomNavItem.Week.screenRoute) {
            WeeklyPage()
        }
        composable(BottomNavItem.Month.screenRoute) {
            MonthlyPageKT()
        }
        composable(BottomNavItem.Todo.screenRoute) {
            TodoPage()
        }
        composable(BottomNavItem.AiManager.screenRoute) {
            // ManagerPage()
            // Test For Edit Plan
            Button(onClick = { navController.navigate(DestinationRoute.EditTodo(id = 2).route) }) {
                Text("EditPage Todo id=2")
            }
        }
        composable(BottomNavItem.Setting.screenRoute) {
            // SettingPage()
            // Test For New Plan
            Button(onClick = { navController.navigate(DestinationRoute.AddSchedule.route) }) {
                Text("EditPage New")
            }
        }
        composable(
            route = "EditPage/{type}?id={id}", arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                },
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            )
        ) { entry ->
            val viewModel: EditPlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
            viewModel.initialize(
                id = entry.arguments?.getString("id")?.toIntOrNull(),
                type = when (entry.arguments?.getString("type")) {
                    "schedule" -> Plan.PlanType.Schedule
                    else       -> Plan.PlanType.Todo
                }
            )

            EditPlanPage(viewModel)
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