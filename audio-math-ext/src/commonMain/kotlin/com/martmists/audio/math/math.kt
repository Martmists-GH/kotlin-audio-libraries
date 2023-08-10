package com.martmists.audio.math

import kotlin.math.PI
import kotlin.math.sin

fun sinc(x: Double): Double {
    return if (x == 0.0) {
        1.0
    } else {
        sin(PI * x) / (PI * x)
    }
}

fun sinc(x: Float): Float {
    return if (x == 0.0f) {
        1.0f
    } else {
        (sin(PI * x) / (PI * x)).toFloat()
    }
}
