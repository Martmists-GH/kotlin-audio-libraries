@file:Suppress("UNCHECKED_CAST")

package com.martmists.audio.sample

import kotlin.math.abs
import kotlin.math.pow


inline fun <reified T : Sample> absFn(): (T) -> T {
    return when (T::class) {
        Byte::class -> { a: Byte -> if (a < 0) -a else a }
        Short::class -> { a: Short -> if (a < 0) -a else a }
        Int::class -> {
            val fn: (Int) -> Int = ::abs
            fn
        }

        Long::class -> {
            val fn: (Long) -> Long = ::abs
            fn
        }

        Float::class -> {
            val fn: (Float) -> Float = ::abs
            fn
        }

        Double::class -> {
            val fn: (Double) -> Double = ::abs
            fn
        }

        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as (T) -> T
}

inline fun <reified T : Sample> powFn(): (T, T) -> T {
    return when (T::class) {
        Byte::class -> { a: Byte, b: Byte -> a.toFloat().pow(b.toInt()).toInt().toByte() }
        Short::class -> { a: Short, b: Short -> a.toFloat().pow(b.toInt()).toInt().toShort() }
        Int::class -> { a: Int, b: Int -> a.toFloat().pow(b).toInt() }
        Long::class -> { a: Long, b: Long -> a.toDouble().pow(b.toDouble()).toLong() }
        Float::class -> {
            val fn: (Float, Float) -> Float = Float::pow
            fn
        }

        Double::class -> {
            val fn: (Double, Double) -> Double = Double::pow
            fn
        }

        else -> throw IllegalArgumentException("Unsupported type ${T::class}")
    } as (T, T) -> T
}
