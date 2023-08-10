package com.martmists.audio.filter

import com.martmists.audio.buffer.ShiftBuffer
import com.martmists.audio.complex.Complex
import com.martmists.audio.filter.util.RootSolver
import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample
import com.martmists.audio.sample.T
import com.martmists.audio.sample.zero
import kotlin.jvm.JvmName

class IIRFilter<T : Sample> @PublishedApi internal constructor(
    private val coefficientsA: DoubleArray,
    private val coefficientsB: DoubleArray,
    private val bufferX: ShiftBuffer<T>,
    private val bufferY: ShiftBuffer<T>,
    private val zero: T,
    private val tFn: Double.() -> T
) : Filter<T> {
    init {
        require(coefficientsA.isNotEmpty() && coefficientsB.isNotEmpty()) { "Number of coefficients must be greater than 0" }
        require(coefficientsA.size == coefficientsB.size) { "Number of coefficients must be equal" }
    }

    override fun processInplace(frame: Frame<T>) {
        require(frame.channels == bufferX.channels && frame.channels == bufferY.channels) { "Number of channels must be equal to the number of channels in the buffer" }

        frame.forEachChannelsIndexed { channel, index, sample ->
            var out = 0.0

            coefficientsA.zip(coefficientsB).forEachIndexed { cIndex, (aCoeff, bCoeff) ->
                if (cIndex != 0) {
                    out += (bufferX[channel, index - 1].toDouble() * bCoeff) - (bufferY[channel, index - 1].toDouble() * aCoeff)
                }
            }

            val input = frame[channel, index]
            frame[channel, index] = ((out + input.toFloat() * coefficientsB[0]) / coefficientsA[0]).tFn()

            bufferX.shift(channel, -1)
            bufferY.shift(channel, -1)
            bufferX[channel, 0] = input
            bufferY[channel, 0] = frame[channel, index]
        }
    }

    fun reset() {
        bufferX.fill(zero)
        bufferY.fill(zero)
    }

    companion object {
        // Solving for B coeffs gives zeroes,
        // Solving for A coeffs gives poles

        inline operator fun <reified T : Sample> invoke(
            channels: Int,
            coeffsA: DoubleArray,
            coeffsB: DoubleArray
        ): IIRFilter<T> {
            return IIRFilter(
                coeffsA,
                coeffsB,
                ShiftBuffer(channels, coeffsA.size + 1),
                ShiftBuffer(channels, coeffsA.size + 1),
                zero(),
                Double::T,
            )
        }

        inline operator fun <reified T : Sample> invoke(
            channels: Int,
            coeffsA: List<Double>,
            coeffsB: List<Double>
        ): IIRFilter<T> {
            return IIRFilter(channels, coeffsA.toDoubleArray(), coeffsB.toDoubleArray())
        }

        @JvmName("invokeRoots")
        inline operator fun <reified T : Sample> invoke(
            channels: Int,
            zeros: List<Complex>,
            poles: List<Complex>
        ): IIRFilter<T> {
            return IIRFilter(channels, RootSolver.solve(poles), RootSolver.solve(zeros))
        }
    }
}
