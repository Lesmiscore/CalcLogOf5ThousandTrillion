package com.nao20010128nao.clo5tt

import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

fun Multiset<BigInteger>.multiplied(): BigInteger =
        elementSet().map { it.pow(count(it)) }.fold(BigInteger.ONE) { acc, bigInteger -> acc * bigInteger }

fun <T> Collection<T>.toMultiset(): Multiset<T> = HashMultiset.create(this)

typealias FactorResult = Map<BigInteger, Multiset<BigInteger>>
typealias MutableFactorResult = MutableMap<BigInteger, Multiset<BigInteger>>

fun fiveThousandTrillionSeq(): Sequence<BigInteger> = run {
    var value = BigInteger.ONE
    val limit = "50000000000000000000".toBigInteger()
    generateSequence { value++ }
            .filter { it <= limit }
}

fun fiveThousandTrillionDecimalSeq(): Sequence<BigDecimal> = run {
    var value = BigDecimal.ONE
    val limit = "50000000000000000000".toBigDecimal()
    generateSequence { value++ }
            .filter { it <= limit }
}

val log10 = CheapMath.ln(10.toBigDecimal(), 10)

operator fun <T> ExecutorService.invoke(callable: Callable<T>): Future<T> = submit(callable)

private val bigIntegerCacheString: MutableMap<String, BigInteger> = mutableMapOf()
private val bigDecimalCacheString: MutableMap<String, BigDecimal> = mutableMapOf()

fun String.toBigInteger(): BigInteger = bigIntegerCacheString.getOrPut(this) { java.math.BigInteger(this) }
fun String.toBigDecimal(): BigDecimal = bigDecimalCacheString.getOrPut(this) { java.math.BigDecimal(this) }
