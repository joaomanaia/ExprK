package com.github.keelar.exprk

import com.github.keelar.exprk.internal.*
import com.github.keelar.exprk.internal.Evaluator
import com.github.keelar.exprk.internal.Expr
import com.github.keelar.exprk.internal.Parser
import com.github.keelar.exprk.internal.Scanner
import com.github.keelar.exprk.internal.Token
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.math.*

class Expressions {
    private val evaluator = Evaluator()

    init {
        define("π", PI)
        define("e", E)

        evaluator.addFunction("ln", object : Function() {
            override fun call(arguments: List<BigDecimal>): BigDecimal {
                if (arguments.size != 1)
                    throw ExpressionException("ln requires one argument")

                return log(arguments.first().doubleValue(), E).toBigDecimal()
            }
        })

        evaluator.addFunction("log", object : Function() {
            override fun call(arguments: List<BigDecimal>): BigDecimal {
                if (arguments.size != 1)
                    throw ExpressionException("log requires one argument")

                return log10(arguments.first().doubleValue()).toBigDecimal()
            }
        })

        evaluator.addFunction("√", object : Function() {
            override fun call(arguments: List<BigDecimal>): BigDecimal {
                if (arguments.size != 1)
                    throw ExpressionException("square root requires one argument")

                return sqrt(arguments.first().doubleValue()).toBigDecimal()
            }
        })
    }

    val precision: Long
        get() = evaluator.decimalMode.decimalPrecision

    val roundingMode: RoundingMode
        get() = evaluator.decimalMode.roundingMode

    fun setPrecision(precision: Long): Expressions {
        evaluator.decimalMode = evaluator.decimalMode.copy(decimalPrecision = precision)

        return this
    }

    fun setRoundingMode(roundingMode: RoundingMode): Expressions {
        evaluator.decimalMode = evaluator.decimalMode.copy(roundingMode = roundingMode)

        return this
    }

    fun define(name: String, value: Long): Expressions {
        define(name, value.toString())

        return this
    }

    fun define(name: String, value: Double): Expressions {
        define(name, value.toString())

        return this
    }

    fun define(name: String, value: BigDecimal): Expressions {
        define(name, value.toPlainString())

        return this
    }

    fun define(name: String, expression: String): Expressions {
        val expr = parse(expression)
        evaluator.define(name, expr)

        return this
    }

    fun addFunction(name: String, function: Function): Expressions {
        evaluator.addFunction(name, function)

        return this
    }

    fun addFunction(name: String, func: (List<BigDecimal>) -> BigDecimal): Expressions {
        evaluator.addFunction(name, object : Function() {
            override fun call(arguments: List<BigDecimal>): BigDecimal {
                return func(arguments)
            }

        })

        return this
    }

    fun eval(expression: String): BigDecimal {
        return evaluator.eval(parse(expression))
    }

    /**
     * eval an expression then round it with {@link Evaluator#mathContext} and call toEngineeringString <br>
     * if error will return message from Throwable
     * @param expression String
     * @return String
     */
    fun evalToString(expression: String): String {
        return try {
            evaluator
                .eval(parse(expression))
                .roundSignificand(evaluator.decimalMode)
                .toPlainString()
        } catch (e: Throwable) {
            e.cause?.message ?: e.message ?: "unknown error"
        }
    }

    private fun parse(expression: String): Expr {
        return parse(scan(expression))
    }

    private fun parse(tokens: List<Token>): Expr {
        return Parser(tokens).parse()
    }

    private fun scan(expression: String): List<Token> {
        return Scanner(expression, evaluator.decimalMode).scanTokens()
    }
}
