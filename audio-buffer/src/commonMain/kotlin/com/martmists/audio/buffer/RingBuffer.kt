package com.martmists.audio.buffer

import com.martmists.audio.sample.Sample
import com.martmists.audio.sample.zero

// An array that can read/write continuously by looping around
class RingBuffer<T : Sample> @PublishedApi internal constructor(
    private val buffer: Array<Array<T>>,
    private val createArray: (size: Int, init: (Int) -> T) -> Array<T>
) {
    init {
        require(buffer.isNotEmpty()) { "Channels must be greater than 0" }
        require(buffer[0].isNotEmpty()) { "Samples must be greater than 0" }
    }

    val channels = buffer.size
    val samples = buffer[0].size

    private val readOffsets = IntArray(channels) { 0 }
    private val writeOffsets = IntArray(channels) { 0 }

    private val Int.next: Int
        get() = (this + 1) % samples

    fun pop(channel: Int): T {
        val idx = readOffsets[channel]
        val value = buffer[channel][idx]
        readOffsets[channel] = idx.next
        return value
    }

    fun pop(channel: Int, amount: Int): Array<T> {
        val values = createArray(amount) {
            pop(channel)
        }
        return values
    }

    fun push(channel: Int, value: T) {
        val idx = writeOffsets[channel]
        buffer[channel][idx] = value
        writeOffsets[channel] = idx.next
    }

    fun push(channel: Int, values: Array<T>) {
        values.forEach {
            push(channel, it)
        }
    }

    fun fill(value: T) {
        buffer.forEach {
            it.fill(value)
        }
    }

    companion object {
        inline operator fun <reified T : Sample> invoke(channels: Int, size: Int): RingBuffer<T> {
            return RingBuffer(Array(channels) {
                Array(size) {
                    zero()
                }
            }, ::Array)
        }
    }
}
