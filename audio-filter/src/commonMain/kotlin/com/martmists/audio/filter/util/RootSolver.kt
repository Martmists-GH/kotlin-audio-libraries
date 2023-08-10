package com.martmists.audio.filter.util

import com.martmists.audio.complex.Complex
import com.martmists.audio.complex.j

/**
 * Utility class for creating polynomial coefficients from roots.
 */
object RootSolver {
    /**
     * Creates a polynomial from the given roots.
     *
     * @param roots The roots of the polynomial.
     * @return The polynomial coefficients, where [0] is the coefficient of the highest power and [size - 1] is the constant.
     */
    @Suppress("ConvertArgumentToSet")
    fun solve(roots: List<Complex>): List<Double> {
        val allRoots = mutableSetOf<Complex>()
        for (root in roots) {
            allRoots.add(root)
            allRoots.add(root.conjugate())
        }

        var p = mutableListOf(1.0 j 0.0)
        for (root in allRoots) {
            val pPrime = MutableList(p.size) { i -> p[i] }
            p.shift()
            p = p - (pPrime * root)
        }
        return p.reversed().map(Complex::real)
    }

    private fun MutableList<Complex>.shift(): MutableList<Complex> {
        return also {
            add(0, 0.j)
        }
    }

    private operator fun MutableList<Complex>.minus(other: List<Complex>): MutableList<Complex> {
        return MutableList(maxOf(size, other.size)) { i ->
            (getOrNull(i) ?: 0.j) - (other.getOrNull(i) ?: 0.j)
        }
    }

    private operator fun MutableList<Complex>.times(other: Complex): MutableList<Complex> {
        return map { it * other }.toMutableList()
    }
}
