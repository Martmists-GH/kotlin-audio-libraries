package com.martmists.audio.complex

import kotlin.math.*

data class Complex(val real: Double, val imag: Double) {
    constructor(real: Number, imag: Number) : this(real.toDouble(), imag.toDouble())
    constructor(real: Number) : this(real.toDouble(), 0.0)

    override fun toString(): String {
        return "($real${if (imag < 0) "" else "+"}${imag}j)"
    }

    fun conjugate(): Complex {
        return Complex(real, -imag)
    }

    operator fun plus(other: Complex): Complex {
        return Complex(real + other.real, imag + other.imag)
    }

    operator fun minus(other: Complex): Complex {
        return Complex(real - other.real, imag - other.imag)
    }

    operator fun times(other: Complex): Complex {
        return Complex(real * other.real - imag * other.imag, real * other.imag + imag * other.real)
    }

    operator fun times(other: Number): Complex {
        return Complex(real * other.toDouble(), imag * other.toDouble())
    }

    operator fun div(other: Complex): Complex {
        val denom = other.real * other.real + other.imag * other.imag
        return Complex((real * other.real + imag * other.imag) / denom, (imag * other.real - real * other.imag) / denom)
    }

    operator fun div(other: Number): Complex {
        return Complex(real / other.toDouble(), imag / other.toDouble())
    }

    operator fun unaryMinus(): Complex {
        return Complex(-real, -imag)
    }

    fun length(): Double {
        return sqrt(real * real + imag * imag)
    }

    fun angle(): Double {
        return atan2(imag, real)
    }

    fun pow(other: Complex): Complex {
        val r = length()
        val theta = angle()
        val a = other.real
        val b = other.imag
        val real = r.pow(a) * exp(-b * theta) * cos(a * theta + b * ln(r))
        val imag = r.pow(a) * exp(-b * theta) * sin(a * theta + b * ln(r))
        return Complex(real, imag)
    }

    fun sqrt(): Complex {
        val r = length()
        val theta = angle()
        val real = sqrt(r) * cos(theta / 2)
        val imag = sqrt(r) * sin(theta / 2)
        return Complex(real, imag)
    }

    companion object {
        val E = Complex(kotlin.math.E, 0.0)
        val PI = Complex(kotlin.math.PI, 0.0)

        fun polar(r: Number, theta: Number): Complex {
            return Complex(r.toDouble() * cos(theta.toDouble()), r.toDouble() * sin(theta.toDouble()))
        }
    }
}

infix fun Number.j(other: Number): Complex {
    return Complex(this, other)
}

val Number.j: Complex
    get() = Complex(0.0, toDouble())
