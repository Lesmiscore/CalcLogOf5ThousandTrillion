package com.nao20010128nao.clo5tt

import java.math.BigDecimal


object CheapMath {
    fun ln(x: BigDecimal, scale: Int): BigDecimal {
        // Check that x > 0.
        if (x.signum() <= 0) {
            throw IllegalArgumentException("x <= 0")
        }

        // The number of digits to the left of the decimal point.
        val magnitude = x.toString().length - x.scale() - 1

        return if (magnitude < 3) {
            lnNewton(x, scale)
        } else {

            // x^(1/magnitude)
            val root = intRoot(x, magnitude.toLong(), scale)

            // ln(x^(1/magnitude))
            val lnRoot = lnNewton(root, scale)

            // magnitude*ln(x^(1/magnitude))
            BigDecimal.valueOf(magnitude.toLong()).multiply(lnRoot)
                    .setScale(scale, BigDecimal.ROUND_HALF_EVEN)
        }// Compute magnitude*ln(x^(1/magnitude)).
    }

    /**
     * Compute the natural logarithm of x to a given scale, x > 0.
     * Use Newton's algorithm.
     */
    private fun lnNewton(x: BigDecimal, scale: Int): BigDecimal {
        var x = x
        val sp1 = scale + 1
        val n = x
        var term: BigDecimal

        // Convergence tolerance = 5*(10^-(scale+1))
        val tolerance = BigDecimal.valueOf(5)
                .movePointLeft(sp1)

        // Loop until the approximations converge
        // (two successive approximations are within the tolerance).
        do {

            // e^x
            val eToX = exp(x, sp1)

            // (e^x - n)/e^x
            term = eToX.subtract(n)
                    .divide(eToX, sp1, BigDecimal.ROUND_DOWN)

            // x - (e^x - n)/e^x
            x = x.subtract(term)
        } while (term > tolerance)

        return x.setScale(scale, BigDecimal.ROUND_HALF_EVEN)
    }

    fun intRoot(x: BigDecimal, index: Long,
                scale: Int): BigDecimal {
        var x = x
        // Check that x >= 0.
        if (x.signum() < 0) {
            throw IllegalArgumentException("x < 0")
        }

        val sp1 = scale + 1
        val n = x
        val i = BigDecimal.valueOf(index)
        val im1 = BigDecimal.valueOf(index - 1)
        val tolerance = BigDecimal.valueOf(5)
                .movePointLeft(sp1)
        var xPrev: BigDecimal

        // The initial approximation is x/index.
        x = x.divide(i, scale, BigDecimal.ROUND_HALF_EVEN)

        // Loop until the approximations converge
        // (two successive approximations are equal after rounding).
        do {
            // x^(index-1)
            val xToIm1 = intPower(x, index - 1, sp1)

            // x^index
            val xToI = x.multiply(xToIm1)
                    .setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // n + (index-1)*(x^index)
            val numerator = n.add(im1.multiply(xToI))
                    .setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // (index*(x^(index-1))
            val denominator = i.multiply(xToIm1)
                    .setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // x = (n + (index-1)*(x^index)) / (index*(x^(index-1)))
            xPrev = x
            x = numerator
                    .divide(denominator, sp1, BigDecimal.ROUND_DOWN)

        } while (x.subtract(xPrev).abs() > tolerance)

        return x
    }

    fun exp(x: BigDecimal, scale: Int): BigDecimal {
        // e^0 = 1
        if (x.signum() == 0) {
            return BigDecimal.ONE
        } else if (x.signum() == -1) {
            return BigDecimal.ONE
                    .divide(exp(x.negate(), scale), scale,
                            BigDecimal.ROUND_HALF_EVEN)
        }// If x is negative, return 1/(e^-x).

        // Compute the whole part of x.
        var xWhole = x.setScale(0, BigDecimal.ROUND_DOWN)

        // If there isn't a whole part, compute and return e^x.
        if (xWhole.signum() == 0) return expTaylor(x, scale)

        // Compute the fraction part of x.
        val xFraction = x.subtract(xWhole)

        // z = 1 + fraction/whole
        val z = BigDecimal.ONE
                .add(xFraction.divide(
                        xWhole, scale,
                        BigDecimal.ROUND_HALF_EVEN))

        // t = e^z
        val t = expTaylor(z, scale)

        val maxLong = BigDecimal.valueOf(java.lang.Long.MAX_VALUE)
        var result = BigDecimal.ONE

        // Compute and return t^whole using intPower().
        // If whole > Long.MAX_VALUE, then first compute products
        // of e^Long.MAX_VALUE.
        while (xWhole >= maxLong) {
            result = result.multiply(
                    intPower(t, java.lang.Long.MAX_VALUE, scale))
                    .setScale(scale, BigDecimal.ROUND_HALF_EVEN)
            xWhole = xWhole.subtract(maxLong)
        }
        return result.multiply(intPower(t, xWhole.toLong(), scale))
                .setScale(scale, BigDecimal.ROUND_HALF_EVEN)
    }

    fun intPower(x: BigDecimal, exponent: Long,
                 scale: Int): BigDecimal {
        var x = x
        var exponent = exponent
        // If the exponent is negative, compute 1/(x^-exponent).
        if (exponent < 0) {
            return BigDecimal.ONE
                    .divide(intPower(x, -exponent, scale), scale,
                            BigDecimal.ROUND_HALF_EVEN)
        }

        var power = BigDecimal.ONE

        // Loop to compute value^exponent.
        while (exponent > 0) {

            // Is the rightmost bit a 1?
            if (exponent and 1 == 1L) {
                power = power.multiply(x)
                        .setScale(scale, BigDecimal.ROUND_HALF_EVEN)
            }

            // Square x and shift exponent 1 bit to the right.
            x = x.multiply(x)
                    .setScale(scale, BigDecimal.ROUND_HALF_EVEN)
            exponent = exponent shr 1
        }

        return power
    }

    private fun expTaylor(x: BigDecimal, scale: Int): BigDecimal {
        var factorial = BigDecimal.ONE
        var xPower = x
        var sumPrev: BigDecimal

        // 1 + x
        var sum = x.add(BigDecimal.ONE)

        // Loop until the sums converge
        // (two successive sums are equal after rounding).
        var i = 2
        do {
            // x^i
            xPower = xPower.multiply(x)
                    .setScale(scale, BigDecimal.ROUND_HALF_EVEN)

            // i!
            factorial = factorial.multiply(BigDecimal.valueOf(i.toLong()))

            // x^i/i!
            val term = xPower
                    .divide(factorial, scale,
                            BigDecimal.ROUND_HALF_EVEN)

            // sum = sum + x^i/i!
            sumPrev = sum
            sum = sum.add(term)

            ++i
        } while (sum.compareTo(sumPrev) != 0)

        return sum
    }
}