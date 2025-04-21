package com.blczy.maltiprac.listening

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShowPsm(id: Int) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(id.toString())
    }
}