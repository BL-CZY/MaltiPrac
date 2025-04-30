package com.blczy.maltiprac.listening

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blczy.maltiprac.LocalNavContext
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.navigation.Route

@Composable
fun ListeningCategories(category: String) {
    val indices = indicesMap[category]
    val navControl = LocalNavContext.current
    indices?.forEach { psm ->
        Button(onClick = {
        }) {
            Text(stringResource(psm.descriptionStringIndex))
        }
    }

    Nav()
}