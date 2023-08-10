@file:Suppress("ReplaceUntilWithRangeUntil")

package com.martmists.audio.examples

import com.martmists.audio.filter.Filter
import com.martmists.audio.frame.Frame
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.flavorDarcula
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class FuzzFilter(private val treshold: Double) : Filter<Double> {
    override fun processInplace(frame: Frame<Double>) {
        for (channel in 0 until frame.channels) {
            for (sample in 0 until frame.samples) {
                val value = frame[channel, sample]
                if (value > treshold) {
                    frame[channel, sample] = 1.0
                } else if (value < -treshold) {
                    frame[channel, sample] = -1.0
                } else {
                    frame[channel, sample] = 0.0
                }
            }
        }
    }
}

fun main() {
    val frame = Frame<Double>(1, 64)
    for (i in 0 until frame.samples) {
        frame[0, i] = Random.nextDouble(-0.5, 0.5) + 0.5 * sin(i * 2 * PI / 32)
    }

    val filter = FuzzFilter(0.001)
    val out = filter.process(frame)

    val signal = letsPlot(
        mapOf(
            "time" to (0 until frame.samples),
            "value" to frame.channel(0),
            "value (fuzz)" to out.channel(0),
        )
    ) + geomLine {
        x = "time"
        y = "value"
    } + geomLine {
        x = "time"
        y = "value (fuzz)"
    } + flavorDarcula()

    signal.show()
}
