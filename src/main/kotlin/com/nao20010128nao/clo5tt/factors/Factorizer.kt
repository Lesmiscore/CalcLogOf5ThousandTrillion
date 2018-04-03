package com.nao20010128nao.clo5tt.factors

import com.nao20010128nao.clo5tt.FactorResult
import java.math.BigInteger

interface Factorizer {
    fun factor(value: Set<BigInteger>): FactorResult
}
