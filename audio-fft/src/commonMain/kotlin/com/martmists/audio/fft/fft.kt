@file:Suppress("ReplaceUntilWithRangeUntil")

package com.martmists.audio.fft

import com.martmists.audio.complex.Complex
import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


// DFT implementation
// Warning: output is not normalized.
// Warning: modifies input array
internal fun doFFT(real: DoubleArray, imag: DoubleArray) {
    require(real.size and (real.size - 1) == 0) { "Real array size must be a power of 2" }
    require(real.size == imag.size) { "Real and imaginary arrays must be the same size" }

    // rearrange
    var target = 0
    for (position in real.indices) {
        if (target > position) {
            val tempReal = real[position]
            val tempImag = imag[position]
            real[position] = real[target]
            imag[position] = imag[target]
            real[target] = tempReal
            imag[target] = tempImag
        }
        var mask = real.size
        while (target and mask.let { mask = it shr 1; mask } != 0) {
            target = target and mask.inv()
        }
        target = target or mask
    }

    // compute
    var step = 1
    while (step < real.size) {
        val jump = step shl 1
        var twiddleReal = 1.0
        var twiddleImag = 0.0

        inner@ for (group in 0 until step) {
            for (pair in group until real.size step jump) {
                val match = pair + step
                val r = real[match]
                val i = imag[match]
                val productReal = twiddleReal * r - twiddleImag * i
                val productImag = twiddleReal * i + twiddleImag * r
                real[match] = real[pair] - productReal
                imag[match] = imag[pair] - productImag
                real[pair] += productReal
                imag[pair] += productImag
            }

            val angle = PI * (group - 1) / step
            twiddleReal = cos(angle)
            twiddleImag = sin(angle)
        }

        step = step shl 1
    }
}

fun fft(input: Array<Complex>): Array<Complex> {
    val real = DoubleArray(input.size) { input[it].real }
    val imag = DoubleArray(input.size) { input[it].imag }
    doFFT(real, imag)
    return Array(input.size) { Complex(real[it], imag[it]) }
}

fun fft(input: Array<out Sample>): Array<Complex> {
    val real = DoubleArray(input.size) { input[it].toDouble() }
    val imag = DoubleArray(input.size) { 0.0 }
    doFFT(real, imag)
    return Array(input.size) { Complex(real[it], imag[it]) }
}

fun fft(input: Frame<*>): Array<Complex> = fft(input.channel(0))
