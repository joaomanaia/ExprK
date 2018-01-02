package com.github.keelar.exprk.internal

import com.github.keelar.exprk.ExpressionException
import com.github.keelar.exprk.internal.TokenType.*
import java.math.BigDecimal

internal class Parser(private val tokens: List<Token>) {

    private var current = 0

    fun parse(): Expr {
        val expr = expression()

        if (!isAtEnd()) {
            throw ExpressionException("Expected end of expression, found '${peek().lexeme}'")
        }

        return expr
    }

    private fun expression(): Expr {
        return or()
    }

    private fun or(): Expr {
        var expr = and()

        while (match(BAR_BAR)) {
            val operator = previous()
            val right = and()

            expr = LogicalExpr(expr, operator, right)
        }

        return expr
    }

    private fun and(): Expr {
        var expr = equality()

        while (match(AMP_AMP)) {
            val operator = previous()
            val right = equality()

            expr = LogicalExpr(expr, operator, right)
        }

        return expr
    }

    private fun equality(): Expr {
        var left = comparison()

        while (match(EQUAL_EQUAL, NOT_EQUAL)) {
            val operator = previous()
            val right = comparison()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun comparison(): Expr {
        var left = addition()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = addition()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun addition(): Expr {
        var left = multiplication()

        while (match(PLUS, MINUS)) {
            val operator = previous()
            val right = multiplication()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun multiplication(): Expr {
        var left = unary()

        while (match(STAR, SLASH, MODULO)) {
            val operator = previous()
            val right = unary()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun unary(): Expr {
        if (match(MINUS)) {
            val operator = previous()
            val right = unary()

            return UnaryExpr(operator, right)
        }

        return exponent()
    }

    private fun exponent(): Expr {
        var left = primary()

        if (match(EXPONENT)) {
            val operator = previous()
            val right = unary()

            left = BinaryExpr(left, operator, right)
        }

        return left
    }

    private fun primary(): Expr {
        if (match(NUMBER)) {
            return LiteralExpr(previous().literal as BigDecimal)
        }

        if (match(IDENTIFIER)) {
            return VariableExpr(previous())
        }

        if (match(LEFT_PAREN)) {
            val expr = expression()

            consume(RIGHT_PAREN, "Expected ')' after '${previous().lexeme}'.")

            return GroupingExpr(expr)
        }

        throw ExpressionException("Expected expression after '${previous().lexeme}'.")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()

                return true
            }
        }

        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw ExpressionException(message)
    }

    private fun check(tokenType: TokenType): Boolean {
        return if (isAtEnd()) {
            false
        } else {
            peek().type === tokenType
        }
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++

        return previous()
    }

    private fun isAtEnd() = peek().type == EOF

    private fun peek() = tokens[current]

    private fun previous() = tokens[current - 1]

}