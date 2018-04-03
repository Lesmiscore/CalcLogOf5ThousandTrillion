package com.nao20010128nao.clo5tt

import com.google.common.collect.HashMultiset
import com.nao20010128nao.clo5tt.factors.FactorCommand
import com.nao20010128nao.clo5tt.factors.Factorizer
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object ByPrimeFactors {
    @JvmStatic
    fun main(args: Array<String>) {
        val factor: Factorizer = FactorCommand
        val exec = Executors.newFixedThreadPool(15)
        val seq = fiveThousandTrillionSeq()
                .chunked(100)
                .map { exec(Callable<FactorResult> { factor.factor(it.toSet()) }) }
                .map { it.get() }
                .flatMap { it.values.asSequence() }
        val finalSet = HashMultiset.create<BigInteger>()
        seq.forEach { finalSet.addAll(it) }
        val result = finalSet.elementSet()
                .map { finalSet.count(it).toBigDecimal() * CheapMath.ln(it.toBigDecimal(), 10) }
                .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc + bigDecimal }
        println(result)
    }
}