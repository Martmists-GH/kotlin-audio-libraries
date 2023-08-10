package com.martmists.audio.buffer

import com.martmists.audio.sample.Sample
import com.martmists.audio.sample.zero

// An array that can shift where it's indexed from
class ShiftBuffer<T : Sample> @PublishedApi internal constructor(
    private val buffer: Array<Array<T>>,
    private val zero: T
) {
    init {
        require(buffer.isNotEmpty()) { "Channels must be greater than 0" }
        require(buffer[0].isNotEmpty()) { "Samples must be greater than 0" }
    }

    val channels = buffer.size
    val samples = buffer[0].size

    private val offsets = IntArray(channels) { 0 }

    operator fun get(channel: Int, index: Int): T {
        return buffer[channel][(index + offsets[channel]) % samples]
    }

    operator fun set(channel: Int, index: Int, value: T) {
        buffer[channel][(index + offsets[channel]) % samples] = value
    }

    fun shift(channel: Int, amount: Int) {
        offsets[channel] += amount
        if (offsets[channel] < 0) {
            shift(channel, samples)
        } else {
            offsets[channel] %= samples
        }
    }

    fun clear() {
        buffer.forEach {
            it.fill(zero)
        }
    }

    fun fill(value: T) {
        buffer.forEach {
            it.fill(value)
        }
    }

    companion object {
        inline operator fun <reified T : Sample> invoke(channels: Int, size: Int): ShiftBuffer<T> {
            return ShiftBuffer(Array(channels) {
                Array(size) {
                    zero()
                }
            }, zero())
        }
    }
}
