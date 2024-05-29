package com.github.keelar.exprk.internal

import com.ionspin.kotlin.bignum.decimal.BigDecimal

abstract class Function {
    abstract fun call(arguments: List<BigDecimal>): BigDecimal
}
