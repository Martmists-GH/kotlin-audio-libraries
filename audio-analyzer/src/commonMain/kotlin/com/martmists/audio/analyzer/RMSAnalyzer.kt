package com.martmists.audio.analyzer

import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample
import kotlin.math.sqrt

class RMSAnalyzer<T : Sample> @PublishedApi internal constructor(
    private val zipMap: Frame<T>.((samples: Array<T>) -> Double) -> Frame<Double>
) : Analyzer<T, Double> {
    override fun analyze(frame: Frame<T>): Double {
        val rms = zipMap(frame) { slice ->
            slice.map {
                it.toDouble()
            }.average()
        }.map {
            it * it
        }.channel(0).average()
        return sqrt(rms)
    }

    companion object {
        inline operator fun <reified T : Sample> invoke(): RMSAnalyzer<T> {
            return RMSAnalyzer(Frame<T>::zipMap)
        }
    }
}
