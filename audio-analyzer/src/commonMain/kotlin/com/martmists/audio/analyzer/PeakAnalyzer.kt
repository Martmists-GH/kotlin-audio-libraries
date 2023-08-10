package com.martmists.audio.analyzer

import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample
import com.martmists.audio.sample.T
import com.martmists.audio.sample.zero
import kotlin.math.abs

class PeakAnalyzer<T : Sample> @PublishedApi internal constructor(
    private val type: PeakDetectionType,
    private val zero: T,
    private val tFn: Double.() -> T
) : Analyzer<T, T> {
    override fun analyze(frame: Frame<T>): T {
        var max = zero

        when (type) {
            PeakDetectionType.FULL_WAVE -> {
                frame.forEach {
                    max = maxOf(max.toDouble(), abs(it.toDouble())).tFn()
                }
            }

            PeakDetectionType.POSITIVE_HALF_WAVE -> {
                frame.forEach {
                    val d = it.toDouble()
                    if (d > 0) {
                        max = maxOf(max.toDouble(), d).tFn()
                    }
                }
            }

            PeakDetectionType.NEGATIVE_HALF_WAVE -> {
                frame.forEach {
                    val d = it.toDouble()
                    if (d < 0) {
                        max = maxOf(max.toDouble(), abs(d)).tFn()
                    }
                }
            }
        }

        return max
    }

    enum class PeakDetectionType {
        FULL_WAVE,
        POSITIVE_HALF_WAVE,
        NEGATIVE_HALF_WAVE,
    }

    companion object {
        inline operator fun <reified T : Sample> invoke(type: PeakDetectionType): PeakAnalyzer<T> {
            return PeakAnalyzer(type, zero(), Double::T)
        }
    }
}
