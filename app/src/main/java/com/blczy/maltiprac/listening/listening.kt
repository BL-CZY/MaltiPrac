package com.blczy.maltiprac.listening

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.ui.theme.MaltiPracTheme

@Composable
fun Listening() {
    Nav()
}

@Preview
@Composable
fun ListeningPreview() {
    MaltiPracTheme {
        Listening()
    }
}