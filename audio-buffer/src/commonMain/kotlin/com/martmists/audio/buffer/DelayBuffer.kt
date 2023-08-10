package com.martmists.audio.buffer

import com.martmists.audio.sample.Sample

// An array that can shift where it's indexed from
class DelayBuffer<T : Sample> @PublishedApi internal constructor(
    private val buffer: ShiftBuffer<T>,
    private val createArray: (Int, (Int) -> T) -> Array<T>
) {
    val channels = buffer.channels
    val delay = buffer.samples

    fun delay(channel: Int, sample: T): T {
        val res = buffer[channel, delay]
        buffer.shift(channel, 1)
        buffer[channel, 0] = sample
        return res
    }

    fun delay(channel: Int, samples: Array<T>): Array<T> {
        require(samples.size <= delay) { "Samples must be less than or equal to delay" }

        return createArray(samples.size) {
            delay(channel, samples[it])
        }
    }

    fun delay(samples: Array<T>): Array<T> {
        require(samples.size % channels == 0) { "Samples must be a multiple of channels" }

        return createArray(samples.size) {
            delay(it, samples[it])
        }
    }

    fun clear() {
        buffer.clear()
    }

    fun fill(value: T) {
        buffer.fill(value)
    }

    companion object {
        inline operator fun <reified T : Sample> invoke(channels: Int, delay: Int): DelayBuffer<T> {
            return DelayBuffer(ShiftBuffer(channels, delay), ::Array)
        }
    }
}
