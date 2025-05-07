package com.blczy.maltiprac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.blczy.maltiprac.home.HomeScreen
import com.blczy.maltiprac.listening.Listening
import com.blczy.maltiprac.listening.ListeningCategories
import com.blczy.maltiprac.listening.ShowPsm
import com.blczy.maltiprac.navigation.ListeningCategoryContext
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

@Composable
fun MainApp() {
    var navContext by remember { mutableStateOf<NavContext>(NavContext.Home()) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (navContext) {
                    is NavContext.Listening -> {
                        navContext = NavContext.Home()
                    }

                    is NavContext.ListeningCategory -> {
                        navContext = NavContext.Listening()
                    }

                    is NavContext.Psm -> {
                        navContext =
                            NavContext.ListeningCategory(context = ListeningCategoryContext(category = (navContext as NavContext.Psm).context.category))
                    }

                    else -> {
                        // tell the system that this callback will be invalidated
                        isEnabled = false
                        backDispatcher?.onBackPressed()
                    }
                }
            }
        }
    }

    // Register and clean up the callback
    DisposableEffect(backDispatcher) {
        backDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }


    MaltiPracTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            SharedTransitionLayout {
                AnimatedContent(navContext, label = "router") { targetState ->
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

                            is NavContext.ListeningCategory -> {
                                ListeningCategories(
                                    targetState.context.category, setNavContext = closure
                                )
                            }

                            is NavContext.Psm -> {
                                ShowPsm(targetState.context.id)
                            }
                        }
                    }
                }
            }
        }
    }
}
