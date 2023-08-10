@file:OptIn(ExperimentalStdlibApi::class)

package com.martmists.audio.window

import com.martmists.audio.frame.Frame
import com.martmists.audio.math.sinc
import com.martmists.audio.sample.*
import kotlin.math.*

class Window<T : Sample> @PublishedApi internal constructor(
    @PublishedApi internal val backed: Array<T>,
    private val timesFn: (T, T) -> T,
    private val mapIndexedFn: Frame<T>.((Int, T) -> T) -> Frame<T>
) {
    init {
        require(backed.isNotEmpty()) { "Window must have at least one sample" }
    }

    val size = backed.size

    @PublishedApi
    internal operator fun T.times(other: T): T {
        return timesFn(this, other)
    }

    fun apply(frame: Frame<T>): Frame<T> {
        require(frame.samples == backed.size) { "Frame must have the same number of samples as the window" }

        return frame.mapIndexedFn { i, value ->
            (value * backed[i])
        }
    }

    fun at(index: Int): T {
        return backed[index]
    }

    companion object {
        @PublishedApi
        internal inline fun <reified T : Sample> Double.scale(min: T, max: T): T {
            require(this in 0.0..1.0) { "Value must be between 0 and 1" }
            require(min.toDouble() < max.toDouble()) { "Min must be less than max" }

            val delta = max.toDouble() - min.toDouble()
            return (min.toDouble() + delta * this).T()
        }

        inline fun <reified T : Sample> make(
            size: Int,
            min: T = zero(),
            max: T = one(),
            init: (Int) -> Double
        ): Window<T> {
            return Window(Array(size) { init(it).scale(min, max) }, timesFn(), Frame<T>::mapIndexed)
        }

        inline fun <reified T : Sample> rectangular(size: Int, value: T = one()): Window<T> {
            return make(size, max = value) { 1.0 }
        }

        inline fun <reified T : Sample> triangular(size: Int, min: T = zero(), max: T = one(), L: Int = 0): Window<T> {
            require(L in 0..<2) { "L must be between 0 and 2" }
            val l = size + L

            return make(size, min, max) {
                1 - abs((it - (size / 2.0)) / (l / 2.0))
            }
        }

        inline fun <reified T : Sample> parzen(size: Int, min: T = zero(), max: T = one()): Window<T> {
            val bigL = size + 1

            return make(size, min, max) {
                when (val n = it - size / 2) {
                    in 0..<bigL / 4 -> 1.0 - 6.0 * (n.toDouble() / bigL - 0.5).pow(2.0)
                    in bigL / 4..<bigL / 2 -> 2.0 * (1.0 - (n.toDouble() / bigL - 0.5).pow(2.0))
                    else -> 0.0
                }
            }
        }

        inline fun <reified T : Sample> welch(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return make(size, min, max) {
                val n = it.toDouble()
                1 - ((n - size / 2) / (size / 2)).pow(2.0)
            }
        }

        inline fun <reified T : Sample> sine(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return make(size, min, max) {
                val delta = it.toDouble() / size
                sin(delta * PI)
            }
        }

        inline fun <reified T : Sample> cosineSum(
            size: Int,
            min: T = zero(),
            max: T = one(),
            vararg coefficients: Double
        ): Window<T> {
            return make(size, min, max) {
                val delta = it.toDouble() / size * 2 * PI
                coefficients.mapIndexed { i, c -> (if (i % 2 == 0) 1 else -1) * c * cos(i * delta) }.sum()
            }
        }

        inline fun <reified T : Sample> hann(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return cosineSum(
                size, min, max,
                0.5,
                0.5,
            )
        }

        inline fun <reified T : Sample> hamming(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return cosineSum(
                size, min, max,
                0.53836,
                0.46164,
            )
        }

        inline fun <reified T : Sample> blackman(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return cosineSum(
                size, min, max,
                7938.0 / 18608,
                9240.0 / 18608,
                1430.0 / 18608,
            )
        }

        inline fun <reified T : Sample> nuttall(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return cosineSum(
                size, min, max,
                0.355768,
                0.487396,
                0.144232,
                0.012604,
            )
        }

        inline fun <reified T : Sample> blackmanNuttall(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return cosineSum(
                size, min, max,
                0.3635819,
                0.4891775,
                0.1365995,
                0.0106411,
            )
        }

        inline fun <reified T : Sample> blackmanHarris(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return cosineSum(
                size, min, max,
                0.35875,
                0.48829,
                0.14128,
                0.01168,
            )
        }

        inline fun <reified T : Sample> gaussian(
            size: Int,
            min: T = zero(),
            max: T = one(),
            sigma: Double = 0.4
        ): Window<T> {
            val hSize = size / 2
            return make(size, min, max) {
                exp(-0.5 * ((it.toDouble() - hSize) / (sigma * hSize)).pow(2))
            }
        }

        inline fun <reified T : Sample> tukey(
            size: Int,
            min: T = zero(),
            max: T = one(),
            alpha: Double = 0.5
        ): Window<T> {
            val hSize = size / 2
            val aSize = alpha * hSize
            return make(size, min, max) {
                when (it.toDouble()) {
                    in 0.0.rangeUntil(aSize) -> 0.5 * (1 + cos(PI * (it - aSize) / alpha))
                    in aSize.rangeTo(hSize.toDouble()) -> 1.0
                    else -> 0.5 * (1 + cos(PI * ((size - it) - aSize) / alpha))
                }
            }
        }

        inline fun <reified T : Sample> planckTaper(
            size: Int,
            min: T = zero(),
            max: T = one(),
            epsilon: Double = 0.5
        ): Window<T> {
            val eSize = epsilon * size
            return make(size, min, max) {
                when {
                    it == 0 -> 0.0
                    it.toDouble() in 1.0.rangeUntil(eSize) -> 1.0 / (1 + exp((eSize / it) - (eSize / (eSize - it))))
                    it.toDouble() in eSize.rangeTo(size - eSize) -> 1.0
                    else -> 1.0 / (1 + exp((eSize / (size - it)) - (eSize / (eSize - (size - it)))))
                }
            }
        }

        inline fun <reified T : Sample> lanczos(size: Int, min: T = zero(), max: T = one()): Window<T> {
            return make(size, min, max) {
                sinc(2 * it.toDouble() / size - 1)
            }
        }
    }
}
