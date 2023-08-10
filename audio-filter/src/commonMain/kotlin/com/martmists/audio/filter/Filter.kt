package com.martmists.audio.filter

import com.martmists.audio.frame.Frame
import com.martmists.audio.sample.Sample

interface Filter<T : Sample> {
    fun process(frame: Frame<T>): Frame<T> = Frame(frame).also(::processInplace)
    fun processInplace(frame: Frame<T>)
}
