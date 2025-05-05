package com.blczy.maltiprac.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blczy.maltiprac.LocalAnimatedVisibilityScope
import com.blczy.maltiprac.LocalSharedTransitionScope
import com.blczy.maltiprac.PreviewWrapper
import com.blczy.maltiprac.R
import com.blczy.maltiprac.navigation.NavContext

@Composable
fun Nav(
    setNavContext: (NavContext) -> Unit = { _ -> },
) {
    val sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope: AnimatedVisibilityScope = LocalAnimatedVisibilityScope.current

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(
                        key = "nav",
                    ), animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxSize(), contentAlignment = Alignment.BottomCenter
        ) {
            BottomNav(setNavContext)
        }
    }
}

@Composable
fun BottomNav(
    setNavContext: (NavContext) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left section - 2 buttons
            Row(
                modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavButton(
                    icon = Icons.Default.Home,
                    label = stringResource(R.string.reading),
                    setNavContext = setNavContext,
                )
                NavButton(
                    icon = Icons.Default.Search,
                    label = stringResource(R.string.listening),
                    setNavContext = setNavContext,
                )
            }

            // Middle section - 1 button
            Box(
                modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
            ) {
                NavButton(
                    icon = Icons.Default.Home,
                    label = "Add",
                    isCenter = true,

                    setNavContext = setNavContext,
                )
            }

            // Right section - 2 buttons
            Row(
                modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavButton(
                    icon = Icons.Default.Notifications,
                    label = stringResource(R.string.speaking),

                    setNavContext = setNavContext,
                )
                NavButton(
                    icon = Icons.Default.Person,
                    label = stringResource(R.string.writing),

                    setNavContext = setNavContext,
                )
            }
        }
    }
}

@Composable
fun NavButton(
    icon: ImageVector, label: String, isCenter: Boolean = false, setNavContext: (NavContext) -> Unit
) {
    val buttonModifier = if (isCenter) {
        Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium
            )
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                if (isCenter) {
                    setNavContext(NavContext.Home())
                }
            }, modifier = buttonModifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isCenter) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        if (!isCenter) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavPreview() {
    PreviewWrapper {
        Nav()
    }
}