package com.blczy.maltiprac.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.blczy.maltiprac.R
import com.blczy.maltiprac.ui.theme.MaltiPracTheme

@Composable
fun Nav() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomNav()
    }
}

@Composable
fun BottomNav() {
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
                modifier = Modifier.weight(2f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavButton(
                    icon = Icons.Default.Home,
                    label = stringResource(R.string.reading)
                )
                NavButton(
                    icon = Icons.Default.Search,
                    label = stringResource(R.string.listening)
                )
            }

            // Middle section - 1 button
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                NavButton(
                    icon = Icons.Default.Home,
                    label = "Add",
                    isCenter = true
                )
            }

            // Right section - 2 buttons
            Row(
                modifier = Modifier.weight(2f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavButton(
                    icon = Icons.Default.Notifications,
                    label = stringResource(R.string.speaking)
                )
                NavButton(
                    icon = Icons.Default.Person,
                    label = stringResource(R.string.writing)
                )
            }
        }
    }
}

@Composable
fun NavButton(
    icon: ImageVector, label: String, isCenter: Boolean = false
) {
    val buttonModifier = if (isCenter) {
        Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
    } else {
        Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { /* Handle click */ }, modifier = buttonModifier
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
    MaltiPracTheme {
        Nav()
    }
}