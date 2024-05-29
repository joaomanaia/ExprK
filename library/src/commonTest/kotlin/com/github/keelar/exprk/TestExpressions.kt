package com.github.keelar.exprk

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class TestExpressions {
    @Test
    fun `test that scientific notation BigDecimals are parsed and equivalent to the plain representation`() {
        val expr = Expressions()
        val scival = "1E+7".toBigDecimal()
        expr.define("SCIVAL", scival)

        assertEquals(
            expected = scival.toPlainString(),
            actual = expr.eval("SCIVAL").toPlainString()
        )
    }

    @Test
    fun `test Scanner will scan scientific form correctly`() {
        val expr = Expressions()
        assertEquals("1e+7".toBigDecimal().toPlainString(), expr.eval("1E+7").toPlainString())
        assertEquals("1e-7".toBigDecimal().toPlainString(), expr.eval("1E-7").toPlainString())
        assertEquals(".101e+2".toBigDecimal().toPlainString(), expr.eval(".101e+2").toPlainString())
        assertEquals(".123e2".toBigDecimal().toPlainString(), expr.eval(".123e2").toPlainString())
        assertEquals("3212.123e-2".toBigDecimal().toPlainString(), expr.eval("3212.123e-2").toPlainString())
    }

    @Test
    fun `test normal expression`() {
        val expr = Expressions()
        assertEquals(
            ".123e2".toBigDecimal().add("3212.123e-2".toBigDecimal()).toPlainString(),
            expr.eval(".123e2+3212.123e-2").toPlainString()
        )
        assertEquals(
            "1e+7".toBigDecimal().minus("52132e-2".toBigDecimal()).toPlainString(),
            expr.eval("1E+7-52132e-2").toPlainString()
        )
    }

    @Test
    fun `test is functions are ignore case`() {
        val expr = Expressions()
        assertEquals(
            listOf(BigDecimal.ONE.negate(), BigDecimal.ZERO, BigDecimal.ONE).minOrNull(),
            expr.eval("mIN(-1,0,1)")
        )

        assertEquals(
            listOf(BigDecimal.ONE.negate(), BigDecimal.ZERO, BigDecimal.ONE).maxOrNull(),
            expr.eval("MaX(-1,0,1)")
        )
    }

    @Test
    fun `test is variables are ignore case`() {
        val expr = Expressions()
        assertEquals(PI.toBigDecimal(), expr.eval("pI"))
        assertEquals(E.toBigDecimal(), expr.eval("E"))
    }
}
