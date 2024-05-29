package com.github.keelar.exprk.internal

import com.ionspin.kotlin.bignum.decimal.BigDecimal

fun interface Function {
    fun call(arguments: List<BigDecimal>): BigDecimal
}
