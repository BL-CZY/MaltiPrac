package com.blczy.maltiprac.listening

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blczy.maltiprac.LocalNavController
import com.blczy.maltiprac.navigation.Route

@Composable
fun ListeningCategories(category: String) {
    val indices = indicesMap[category]
    val navControl = LocalNavController.current
    indices?.forEach { psm ->
        Button(onClick = {
            navControl.navigate("${Route.LISTENING_PSM.route}/${psm.index}")
        }) {
            Text(stringResource(psm.descriptionStringIndex))
        }
    }
}