package com.blczy.maltiprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.blczy.maltiprac.home.HomeScreen
import com.blczy.maltiprac.listening.Listening
import com.blczy.maltiprac.navigation.NavContext
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

var LocalNavContext = compositionLocalOf<NavContext> {
    error(
        "No NavContext found"
    )
}

var LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    error(
        "No SharedTransitionScope found"
    )
}

var LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope> {
    error(
        "No AnimatedVisibilityScope found"
    )
}

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    MaltiPracTheme {
        var navContext = NavContext.Home()
        CompositionLocalProvider(LocalNavContext provides navContext) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainApp() {
    var navContext by remember { mutableStateOf<NavContext>(NavContext.Home()) }
    MaltiPracTheme {
        SharedTransitionLayout {
            AnimatedContent(navContext, label = "test") { targetState ->
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalAnimatedVisibilityScope provides this@AnimatedContent,
                    LocalNavContext provides navContext,
                ) {
                    val closure: (NavContext) -> Unit = { value -> navContext = value }

                    when (targetState) {
                        is NavContext.Home -> {
                            HomeScreen(setNavContext = closure)
                        }

                        is NavContext.Listening -> {
                            Listening(setNavContext = closure)
                        }
                    }
                }
            }
        }
    }
}
