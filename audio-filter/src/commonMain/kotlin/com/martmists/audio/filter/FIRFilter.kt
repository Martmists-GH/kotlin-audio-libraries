package com.martmists.audio.filter

import com.martmists.audio.buffer.ShiftBuffer
import com.martmists.audio.complex.Complex
import com.martmists.audio.filter.util.RootSolver
import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample
import com.martmists.audio.sample.T
import com.martmists.audio.sample.zero
import kotlin.jvm.JvmName

class FIRFilter<T : Sample> @PublishedApi internal constructor(
    private val coefficients: DoubleArray,
    private val buffer: ShiftBuffer<T>, private val zero: T,
    private val tFn: Double.() -> T
) : Filter<T> {
    init {
        require(coefficients.isNotEmpty()) { "Number of coefficients must be greater than 0" }
    }

    override fun processInplace(frame: Frame<T>) {
        require(frame.channels == buffer.channels) { "Number of channels must be equal to the number of channels in the buffer" }

        frame.forEachChannelsIndexed { channel, index, sample ->
            buffer.shift(channel, -1)
            buffer[channel, 0] = sample

            frame[channel, index] = coefficients.mapIndexed { cIndex, coeff ->
                coeff * buffer[channel, cIndex].toDouble()
            }.sum().tFn()
        }
    }

    fun reset() {
        buffer.fill(zero)
    }

    companion object {
        inline operator fun <reified T : Sample> invoke(channels: Int, coefficients: DoubleArray): FIRFilter<T> {
            return FIRFilter(coefficients, ShiftBuffer(channels, coefficients.size + 1), zero(), Double::T)
        }

        @JvmName("invokeVararg")
        inline operator fun <reified T : Sample> invoke(channels: Int, vararg coefficients: Double): FIRFilter<T> {
            return FIRFilter(channels, coefficients)
        }

        inline operator fun <reified T : Sample> invoke(channels: Int, coefficients: List<Double>): FIRFilter<T> {
            return FIRFilter(channels, coefficients.toDoubleArray())
        }

        @JvmName("invokeRoots")
        inline operator fun <reified T : Sample> invoke(channels: Int, zeros: List<Complex>): FIRFilter<T> {
            return FIRFilter(channels, RootSolver.solve(zeros))
        }
    }
}
