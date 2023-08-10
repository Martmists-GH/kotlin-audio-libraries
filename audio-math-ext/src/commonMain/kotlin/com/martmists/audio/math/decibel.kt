package com.martmists.audio.math

import kotlin.math.*

fun Double.toDecibels(): Double {
    return 20 * log10(abs(this))
}

fun Number.toDecibels(): Double {
    return toDouble().toDecibels()
}

fun Double.fromDecibels(): Double {
    return 10.0.pow(this / 20.0)
}

fun Number.fromDecibels(): Double {
    return toDouble().fromDecibels()
}

// Alternative, faster implementations
// Effectively the same but with some precalculated constants
// Speed advantage may vary depending on the platform
// On JVM and WASM, the fast version is about 1.2x-2x faster
// On JS (Legacy and IR), they're about the same speed
// That said, I can't imagine many scenarios where this would be a bottleneck where a faster version would be needed
// But they're here in the rare case that this is essential.

val toDB = 20.0 / ln(10.0)
fun Double.toDecibelsFast(): Double {
    return ln(this) * toDB
}

val fromDB = ln(10.0) / 20.0
fun Double.fromDecibelsFast(): Double {
    return exp(this * fromDB)
}
