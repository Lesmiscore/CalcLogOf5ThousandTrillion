package com.nao20010128nao.clo5tt.factors

import com.google.common.collect.Multiset
import com.nao20010128nao.clo5tt.FactorResult
import com.nao20010128nao.clo5tt.MutableFactorResult
import com.nao20010128nao.clo5tt.toMultiset
import java.math.BigInteger

object ManagedFactor1 : Factorizer {
    override fun factor(value: Set<BigInteger>): FactorResult =
            value.map { it to factor(it) }.toMap()

    private val memory: MutableFactorResult = mutableMapOf()

    private fun factor(n: BigInteger): Multiset<BigInteger> = memory.getOrPut(n) { tdFactors(n).toMultiset() }

    private val two = BigInteger.valueOf(2)
    private val three = BigInteger.valueOf(3)

    private fun tdFactors(n: BigInteger): List<BigInteger> {
        var n = n
        val fs = mutableListOf<BigInteger>()

        if (n < two) {
            throw IllegalArgumentException("must be greater than one")
        }

        while (n.mod(two) == BigInteger.ZERO) {
            fs.add(two)
            n = n.divide(two)
        }

        if (n > BigInteger.ONE) {
            var f = three
            while (f.multiply(f) <= n) {
                if (n.mod(f) == BigInteger.ZERO) {
                    fs.add(f)
                    n = n.divide(f)
                } else {
                    f = f.add(two)
                }
            }
            fs.add(n)
        }

        return fs
    }
}