package com.martmists.audio.examples

import com.martmists.audio.complex.Complex
import com.martmists.audio.fft.fft
import com.martmists.audio.filter.FIRFilter
import com.martmists.audio.frame.Frame
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.gggrid
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleXLog10
import org.jetbrains.letsPlot.scale.scaleYLog10
import org.jetbrains.letsPlot.themes.flavorDarcula

fun main() {
    val frame = Frame<Double>(1, 1024)
    frame[0, 0] = 1.0

    val filter = FIRFilter<Double>(1, 0.5, 1.0)
    val out = filter.process(frame)

    val fftData = fft(out)
    val freq = letsPlot(
        mapOf(
            "frequency" to fftData.indices,
            "gain (dB)" to fftData.map(Complex::length),
        )
    ) + geomLine {
        x = "frequency"
        y = "gain (dB)"
    } + scaleXLog10() + scaleYLog10()
    val phase = letsPlot(
        mapOf(
            "frequency" to fftData.indices,
            "phase (rad)" to fftData.map(Complex::angle),
        )
    ) + geomLine {
        x = "frequency"
        y = "phase (rad)"
    } + scaleXLog10()
    val grid = gggrid(
        plots = listOf(freq, phase).map { it + flavorDarcula() },
        ncol = 1,
    )
    grid.show()
}
