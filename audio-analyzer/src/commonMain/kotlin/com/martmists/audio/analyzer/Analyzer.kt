package com.martmists.audio.analyzer

import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample

interface Analyzer<T : Sample, D> {
    fun analyze(frame: Frame<T>): D
}
