package com.martmists.audio.sample

typealias Sample = Number

inline fun <reified T : Sample> zero(): T {
    return when (T::class) {
        Byte::class -> 0.toByte()
        Short::class -> 0.toShort()
        Int::class -> 0
        Long::class -> 0L
        Float::class -> 0f
        Double::class -> 0.0
        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as T
}

inline fun <reified T : Sample> one(): T {
    return when (T::class) {
        Byte::class -> 1.toByte()
        Short::class -> 1.toShort()
        Int::class -> 1
        Long::class -> 1L
        Float::class -> 1f
        Double::class -> 1.0
        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as T
}

inline fun <reified T : Sample> Number.T(): T {
    return when (T::class) {
        Byte::class -> toByte()
        Short::class -> toShort()
        Int::class -> toInt()
        Long::class -> toLong()
        Float::class -> toFloat()
        Double::class -> toDouble()
        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as T
}
