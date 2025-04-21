package com.blczy.maltiprac.animation

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

fun <T> springTransition(): SpringSpec<T> {
    return spring<T>(
        dampingRatio = 0.85f,
        stiffness = 100.0f,
    )
}
