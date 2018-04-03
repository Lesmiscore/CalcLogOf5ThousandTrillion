package com.nao20010128nao.clo5tt

import java.math.BigDecimal

object SimpleWay {
    @JvmStatic
    fun main(args: Array<String>) {
        val seq = fiveThousandTrillionDecimalSeq()
        val result = seq.drop(1).map { CheapMath.ln(it, 10) }
                .fold(BigDecimal.ZERO) { acc, bigDecimal -> acc + bigDecimal } / log10
        println(result)
    }
}