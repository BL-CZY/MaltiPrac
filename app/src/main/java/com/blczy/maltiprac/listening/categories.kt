package com.blczy.maltiprac.listening

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.navigation.NavContext
import com.blczy.maltiprac.navigation.PsmContext

enum class Category {
    Shopping
}

@Composable
fun ListeningCategories(
    category: Category,
    setNavContext: (NavContext) -> Unit,
) {
    val indices = indicesMap[category]

    indices?.forEach { psm ->
        Button(onClick = {
            setNavContext(
                NavContext.Psm(
                    context = PsmContext(
                        id = psm.index, category = category
                    )
                )
            )
        }) {
            Text(stringResource(psm.descriptionStringIndex))
        }
    }

    Nav(setNavContext)
}