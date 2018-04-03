package com.nao20010128nao.clo5tt.factors

import com.google.common.collect.Multiset
import com.nao20010128nao.clo5tt.FactorResult
import com.nao20010128nao.clo5tt.toMultiset
import java.math.BigInteger

object FactorCommand : Factorizer {
    override fun factor(value: Set<BigInteger>): FactorResult {
        val args = listOf("factor") + value.sorted().map { it.toString() }
        val read = ProcessBuilder(args).start().inputStream.reader().readLines()
        val result = mutableMapOf<BigInteger, Multiset<BigInteger>>()
        read.filter { it.contains(": ") }.forEach {
            val (num, primes) = it.split(": ")
            result[num.toBigInteger()] = primes.split(" ").map { it.toBigInteger() }.toMultiset()
        }
        return result
    }
}
