package com.blczy.maltiprac.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blczy.maltiprac.R
import com.blczy.maltiprac.components.Nav
import com.blczy.maltiprac.ui.theme.MaltiPracTheme

@Composable
fun GridButton(text: String) {
    Button(
        onClick = {},
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .aspectRatio(1f) // Makes the button square by setting equal width and height
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Makes the grid take up 80% of screen width
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.TopCenter),
                    textAlign = TextAlign.Center
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 48.dp) // Add some space below title
                ) {
                    item {
                        GridButton(stringResource(R.string.reading))
                    }
                    item {
                        GridButton(stringResource(R.string.listening))
                    }
                    item {
                        GridButton(stringResource(R.string.speaking))
                    }
                    item {
                        GridButton(stringResource(R.string.writing))
                    }
                }
            }
        }

        // Nav at the bottom of the screen
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Nav()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MaltiPracTheme {
        HomeScreen()
    }
}
