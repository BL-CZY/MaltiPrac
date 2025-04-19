package com.blczy.maltiprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blczy.maltiprac.home.HomeScreen
import com.blczy.maltiprac.listening.Listening
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
                navController = navController, startDestination = "home"
            ) {
                composable(route = "home", enterTransition = {
                    when (initialState.destination.route) {
                        "listening" -> slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                        else -> null
                    }
                }, exitTransition = {
                    when (targetState.destination.route) {
                        "listening" -> slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )

                        else -> null
                    }
                }) {
                    HomeScreen()
                }
                composable(route = "listening", enterTransition = {
                    when (initialState.destination.route) {
                        "home" -> slideIntoContainer(
                            AnimatedContentTransitionScope
                                .SlideDirection.Up,
                            animationSpec = tween(700)
                        )

                        else -> null
                    }
                }, exitTransition = {
                    when (targetState.destination.route) {
                        "home" -> slideOutOfContainer(
                            AnimatedContentTransitionScope
                                .SlideDirection.Down,
                            animationSpec = tween(700)
                        )

                        else -> null
                    }
                }) {
                    Listening()
                }
            }
        }
    }
}