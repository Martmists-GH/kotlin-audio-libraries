@file:Suppress("UNCHECKED_CAST")

package com.martmists.audio.sample

inline fun <reified T : Sample> plusFn(): (T, T) -> T {
    return when (T::class) {
        Byte::class -> { a: Byte, b: Byte -> (a + b).toByte() }
        Short::class -> { a: Short, b: Short -> (a + b).toShort() }
        Int::class -> {
            val fn: (Int, Int) -> Int = Int::plus
            fn
        }

        Long::class -> {
            val fn: (Long, Long) -> Long = Long::plus
            fn
        }

        Float::class -> {
            val fn: (Float, Float) -> Float = Float::plus
            fn
        }

        Double::class -> {
            val fn: (Double, Double) -> Double = Double::plus
            fn
        }

        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as (T, T) -> T
}

inline fun <reified T : Sample> minusFn(): (T, T) -> T {
    return when (T::class) {
        Byte::class -> { a: Byte, b: Byte -> (a - b).toByte() }
        Short::class -> { a: Short, b: Short -> (a - b).toShort() }
        Int::class -> {
            val fn: (Int, Int) -> Int = Int::minus
            fn
        }

        Long::class -> {
            val fn: (Long, Long) -> Long = Long::minus
            fn
        }

        Float::class -> {
            val fn: (Float, Float) -> Float = Float::minus
            fn
        }

        Double::class -> {
            val fn: (Double, Double) -> Double = Double::minus
            fn
        }

        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as (T, T) -> T
}

inline fun <reified T : Sample> timesFn(): (T, T) -> T {
    return when (T::class) {
        Byte::class -> { a: Byte, b: Byte -> (a * b).toByte() }
        Short::class -> { a: Short, b: Short -> (a * b).toShort() }
        Int::class -> {
            val fn: (Int, Int) -> Int = Int::times
            fn
        }

        Long::class -> {
            val fn: (Long, Long) -> Long = Long::times
            fn
        }

        Float::class -> {
            val fn: (Float, Float) -> Float = Float::times
            fn
        }

        Double::class -> {
            val fn: (Double, Double) -> Double = Double::times
            fn
        }

        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as (T, T) -> T
}


inline fun <reified T : Sample> divFn(): (T, T) -> T {
    return when (T::class) {
        Byte::class -> { a: Byte, b: Byte -> (a / b).toByte() }
        Short::class -> { a: Short, b: Short -> (a / b).toShort() }
        Int::class -> {
            val fn: (Int, Int) -> Int = Int::div
            fn
        }

        Long::class -> {
            val fn: (Long, Long) -> Long = Long::div
            fn
        }

        Float::class -> {
            val fn: (Float, Float) -> Float = Float::div
            fn
        }

        Double::class -> {
            val fn: (Double, Double) -> Double = Double::div
            fn
        }

        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as (T, T) -> T
}
