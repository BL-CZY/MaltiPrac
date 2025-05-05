package com.blczy.maltiprac.listening

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blczy.maltiprac.PreviewWrapper
import com.blczy.maltiprac.R
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.navigation.ListeningCategoryContext
import com.blczy.maltiprac.navigation.NavContext

@Composable
fun Listening(
    setNavContext: (NavContext) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text("PSMs: ")
        Button(onClick = {
            setNavContext(
                NavContext.ListeningCategory(
                    context = ListeningCategoryContext(
                        category = Category.Shopping
                    )
                )
            )
        }) {
            Text(stringResource(R.string.psm_topic_shopping))
        }
    }

    Nav(setNavContext = setNavContext)
}

@Preview
@Composable
fun ListeningPreview() {
    PreviewWrapper {
        Listening(setNavContext = { _ -> })
    }
}