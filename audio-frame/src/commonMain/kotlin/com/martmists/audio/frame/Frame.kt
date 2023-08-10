package com.martmists.audio.frame

import com.martmists.audio.sample.Sample
import com.martmists.audio.sample.zero
import kotlin.jvm.JvmName

class Frame<T : Sample>(
    @PublishedApi internal val backed: Array<Array<T>>,
    private val zero: T,
    @PublishedApi internal val createArray: (size: Int, init: (Int) -> T) -> Array<T>,
    private val createCopyArray: (size: Int, init: (Int) -> Array<T>) -> Array<Array<T>>,
    private val createCopyFrame: (Array<Array<T>>) -> Frame<T>,
) {
    init {
        require(backed.isNotEmpty()) { "Frame must have at least one channel" }
        require(backed[0].isNotEmpty()) { "Frame must have at least one sample" }
        require(backed.all { it.size == backed[0].size }) { "All channels must have the same number of samples" }
    }

    val channels = backed.size
    val samples = backed[0].size

    fun copy(): Frame<T> {
        return createCopyFrame(createCopyArray(channels) { backed[it].copyOf() })
    }

    fun channel(channel: Int): Array<T> {
        return backed[channel]
    }

    operator fun get(channel: Int): Array<T> {
        return backed[channel]
    }

    operator fun get(channel: Int, sample: Int): T {
        return backed[channel][sample]
    }

    operator fun get(channel: Int, range: IntRange): Array<T> {
        return backed[channel].copyOfRange(range.first, range.last)
    }

    operator fun set(channel: Int, sample: Int, value: T) {
        backed[channel][sample] = value
    }

    operator fun set(channel: Int, range: IntRange, value: Array<T>) {
        require(value.size == range.last - range.first + 1) { "Value must be the same size as the range" }
        for (i in range) {
            backed[channel][i] = value[i - range.first]
        }
    }

    fun clear() = fill(zero)

    fun fill(value: T, fromIndex: Int = 0, toIndex: Int = samples) {
        backed.forEach { it.fill(value, fromIndex, toIndex) }
    }

    inline fun <reified R : Sample> map(transform: (sample: T) -> R): Frame<R> {
        return Frame(backed.map { it.map(transform) })
    }

    inline fun <reified R : Sample> mapIndexed(transform: (index: Int, sample: T) -> R): Frame<R> {
        return Frame(backed.map { it.mapIndexed(transform) })
    }

    inline fun <reified R : Sample> mapChannels(transform: (channel: Int, sample: T) -> R): Frame<R> {
        return Frame(backed.mapIndexed { i, array -> array.map { transform(i, it) } })
    }

    inline fun <reified R : Sample> mapChannelsIndexed(transform: (channel: Int, index: Int, sample: T) -> R): Frame<R> {
        return Frame(backed.mapIndexed { i, array -> array.mapIndexed { j, value -> transform(i, j, value) } })
    }

    fun forEach(block: (sample: T) -> Unit) {
        backed.forEach {
            it.forEach(block)
        }
    }

    fun forEachIndexed(block: (index: Int, sample: T) -> Unit) {
        backed.forEach {
            it.forEachIndexed(block)
        }
    }

    fun forEachChannels(block: (channel: Int, sample: T) -> Unit) {
        backed.forEachIndexed { channel, array ->
            array.forEach {
                block(channel, it)
            }
        }
    }

    fun forEachChannelsIndexed(block: (channel: Int, index: Int, sample: T) -> Unit) {
        backed.forEachIndexed { channel, array ->
            array.forEachIndexed { index, sample ->
                block(channel, index, sample)
            }
        }
    }

    inline fun <reified R : Sample> zipMap(transform: (samples: Array<T>) -> Array<R>): Frame<R> {
        val res = Array(channels) { Array<R>(samples) { zero() } }

        for (s in 0..<samples) {
            val out = transform(createArray(channels) { backed[it][s] })
            for (c in 0..<channels) {
                res[c][s] = out[c]
            }
        }

        return Frame(res)
    }

    @JvmName("zipMapSingle")
    inline fun <reified R : Sample> zipMap(transform: (samples: Array<T>) -> R): Frame<R> {
        val res = Array(channels) { Array<R>(samples) { zero() } }

        for (s in 0..<samples) {
            val out = transform(createArray(channels) { backed[it][s] })
            for (c in 0..<channels) {
                res[c][s] = out
            }
        }

        return Frame(res)
    }

    inline fun <reified R : Sample> zipMapIndexed(transform: (index: Int, samples: Array<T>) -> Array<R>): Frame<R> {
        val res = Array(channels) { Array<R>(samples) { zero() } }

        for (s in 0..<samples) {
            val out = transform(s, createArray(channels) { backed[it][s] })
            for (c in 0..<channels) {
                res[c][s] = out[c]
            }
        }

        return Frame(res)
    }

    @JvmName("zipMapIndexedSingle")
    inline fun <reified R : Sample> zipMapIndexed(transform: (index: Int, samples: Array<T>) -> R): Frame<R> {
        val res = Array(channels) { Array<R>(samples) { zero() } }

        for (s in 0..<samples) {
            val out = transform(s, createArray(channels) { backed[it][s] })
            for (c in 0..<channels) {
                res[c][s] = out
            }
        }

        return Frame(res)
    }

    companion object {
        @PublishedApi
        internal inline operator fun <reified T : Sample> invoke(array: Array<Array<T>>): Frame<T> {
            val zero = zero<T>()
            val createArray: (Int, (Int) -> T) -> Array<T> = ::Array
            val createArray2: (Int, (Int) -> Array<T>) -> Array<Array<T>> = ::Array
            lateinit var createFrame: (Array<Array<T>>) -> Frame<T>
            createFrame = {
                Frame(it, zero, createArray, createArray2, createFrame)
            }

            return Frame(array, zero, ::Array, ::Array, createFrame)
        }

        inline operator fun <reified T : Sample> invoke(channels: Int, samples: Int): Frame<T> {
            val zero = zero<T>()
            return Frame(Array(channels) { Array(samples) { zero } })
        }

        operator fun <T : Sample> invoke(from: Frame<T>): Frame<T> {
            return from.copy()
        }

        @JvmName("invokeVararg")
        inline operator fun <reified T : Sample> invoke(vararg from: Array<T>): Frame<T> {
            return Frame(from.map(Array<T>::copyOf).toTypedArray())
        }

        inline operator fun <reified T : Sample> invoke(from: List<List<T>>): Frame<T> {
            return Frame(from.map(Collection<T>::toTypedArray).toTypedArray())
        }

        // <T>Array implementations

        operator fun invoke(vararg from: DoubleArray): Frame<Double> {
            return Frame(Array(from.size) { from[it].toTypedArray() })
        }

        operator fun invoke(vararg from: FloatArray): Frame<Float> {
            return Frame(Array(from.size) { from[it].toTypedArray() })
        }

        operator fun invoke(vararg from: IntArray): Frame<Int> {
            return Frame(Array(from.size) { from[it].toTypedArray() })
        }

        operator fun invoke(vararg from: LongArray): Frame<Long> {
            return Frame(Array(from.size) { from[it].toTypedArray() })
        }

        operator fun invoke(vararg from: ShortArray): Frame<Short> {
            return Frame(Array(from.size) { from[it].toTypedArray() })
        }

        operator fun invoke(vararg from: ByteArray): Frame<Byte> {
            return Frame(Array(from.size) { from[it].toTypedArray() })
        }
    }
}
