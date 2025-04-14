package com.blczy.maltiprac.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blczy.maltiprac.R
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.ui.theme.MaltiPracTheme

@Composable
fun HomeScreen() {
    Text(stringResource(R.string.title))
    Nav()
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MaltiPracTheme {
        HomeScreen()
    }
}