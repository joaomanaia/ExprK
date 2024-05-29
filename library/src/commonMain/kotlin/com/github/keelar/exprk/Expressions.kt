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
        define("pi", PI)
        define("e", E)

        evaluator.addFunction("abs") { arguments ->
            require(arguments.size == 1) { "abs requires one argument" }

            arguments.first().abs()
        }

        evaluator.addFunction("sum") { arguments ->
            require(arguments.isNotEmpty()) { "sum requires at least one argument" }

            arguments.reduce { sum, bigDecimal ->
                sum.add(bigDecimal)
            }
        }

        evaluator.addFunction("avg") { arguments ->
            require(arguments.isNotEmpty()) { "avg requires at least one argument" }

            arguments.reduce { sum, bigDecimal ->
                sum.add(bigDecimal)
            }.divide(arguments.size.toBigDecimal())
        }

        evaluator.addFunction("floor") { arguments ->
            require(arguments.size == 1) { "floor requires one argument" }

            val mode = evaluator.decimalMode.copy(roundingMode = RoundingMode.FLOOR)
            arguments.first().roundSignificand(mode)
        }

        evaluator.addFunction("ceil") { arguments ->
            require(arguments.size == 1) { "ceil requires one argument" }

            val mode = evaluator.decimalMode.copy(roundingMode = RoundingMode.CEILING)
            arguments.first().roundSignificand(mode)
        }

        evaluator.addFunction("round") { arguments ->
            require(arguments.size == 1) { "round requires one or two arguments" }

            // If no scale is provided, round to the nearest integer
            val scale = if (arguments.size == 2) arguments[1].longValue() else 0L

            val mode = evaluator.decimalMode.copy(decimalPrecision = scale)
            arguments.first().roundSignificand(mode)
        }

        evaluator.addFunction("min") { arguments ->
            require(arguments.isNotEmpty()) { "min requires at least one argument" }

            arguments.minOrNull()!!
        }

        evaluator.addFunction("max") { arguments ->
            require(arguments.isNotEmpty()) { "max requires at least one argument" }

            arguments.maxOrNull()!!
        }

        evaluator.addFunction("if") { arguments ->
            require(arguments.size == 3) { "if requires three arguments" }

            val condition = arguments[0]
            val thenValue = arguments[1]
            val elseValue = arguments[2]

            if (condition != BigDecimal.ZERO) {
                thenValue
            } else {
                elseValue
            }
        }

        evaluator.addFunction("ln") { arguments ->
            require(arguments.size == 1) { "ln requires one argument" }

            ln(arguments.first().doubleValue()).toBigDecimal()
        }

        evaluator.addFunction("log") { arguments ->
            require(arguments.size == 1) { "log requires one argument" }

            log10(arguments.first().doubleValue()).toBigDecimal()
        }

        evaluator.addFunction("√") { arguments ->
            require(arguments.size == 1) { "√ requires one argument" }

            sqrt(arguments.first().doubleValue()).toBigDecimal()
        }
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

    fun addFunction(name: String, func: (List<BigDecimal>) -> BigDecimal): Expressions {
        evaluator.addFunction(name) { arguments -> func(arguments) }

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
