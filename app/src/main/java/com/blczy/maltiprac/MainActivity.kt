package com.blczy.maltiprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blczy.maltiprac.animation.springTransition
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.home.HomeScreen
import com.blczy.maltiprac.listening.Listening
import com.blczy.maltiprac.listening.ListeningCategories
import com.blczy.maltiprac.listening.ShowPsm
import com.blczy.maltiprac.navigation.Route
import com.blczy.maltiprac.ui.theme.MaltiPracTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

val LocalNavController = compositionLocalOf<NavController> {
    error(
        "No NavController " + "found" + ""
    )
}

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    MaltiPracTheme {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavController provides navController) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainApp() {
    MaltiPracTheme {
        val navController = rememberNavController()
        CompositionLocalProvider(
            LocalNavController provides navController
        ) {
            NavHost(
                navController = navController, startDestination = Route.HOME.route
            ) {
                composable(
                    route = Route.HOME.route,
                    enterTransition = {
                        when (initialState.destination.route) {
                            Route.LISTENING.route,
                            Route.LISTENING_CATEGORIES.route,
                            "${Route.LISTENING_CATEGORIES.route}/{category}",
                            Route.LISTENING_PSM.route,
                            "${Route.LISTENING_PSM.route}/{id}" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            Route.LISTENING.route ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    }
                ) {
                    HomeScreen()
                }

                composable(
                    route = Route.LISTENING.route,
                    enterTransition = {
                        when (initialState.destination.route) {
                            Route.HOME.route ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = springTransition()
                                )
                            "${Route.LISTENING_CATEGORIES.route}/{category}" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "${Route.LISTENING_CATEGORIES.route}/{category}" ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    }
                ) {
                    Listening()
                }

                // Categories Screen
                composable(
                    route = "${Route.LISTENING_CATEGORIES.route}/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType }),
                    enterTransition = {
                        when (initialState.destination.route) {
                            Route.LISTENING.route ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = springTransition()
                                )
                            "${Route.LISTENING_PSM.route}/{id}" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            Route.LISTENING.route ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = springTransition()
                                )
                            "${Route.LISTENING_PSM.route}/{id}" ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    }
                ) { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category")
                    ListeningCategories(category ?: "")
                }

                // PSM Screen
                composable(
                    route = "${Route.LISTENING_PSM.route}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                    enterTransition = {
                        when (initialState.destination.route) {
                            "${Route.LISTENING_CATEGORIES.route}/{category}" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "${Route.LISTENING_CATEGORIES.route}/{category}" ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = springTransition()
                                )
                            else -> null
                        }
                    }
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id")
                    ShowPsm(id ?: 0)
                }
            }
            Nav()
        }
    }
}
