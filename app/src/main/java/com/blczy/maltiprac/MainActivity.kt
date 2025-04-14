package com.blczy.maltiprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blczy.maltiprac.home.HomeScreen
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
        "No NavController " +
                "found" +
                ""
    )
}

@Preview(showBackground = true)
@Composable
fun MainApp() {
    MaltiPracTheme {
        val navController = rememberNavController()
        CompositionLocalProvider(
            LocalNavController provides
                    navController
        ) {
            NavHost(
                navController = navController, startDestination =
                    "home"
            ) {
                composable("home") {
                    HomeScreen()
                }
            }
        }
    }
}