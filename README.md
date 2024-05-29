# ExprK

A simple Kotlin Multiplatform mathematical expression evaluator.

### Features:

* Uses BigDecimal for calculations and results
* Allows you to define variables using values or expressions
* Variable definition expressions can reference previously defined variables
* Configurable precision and rounding mode
* Functions and the ability to define new ones

### Supported operators

#### Arithmetic operators

| Name        | Operator |
|-------------|----------|
| Plus        | +        |
| Minus       | -        |
| Multiply    | *        |
| Divide      | /        |
| Modulus     | %        |
| Exponent    | ^        |
| Square root | √        |

#### Logical operators

| Name | Operator |
|------|----------|
| And  | &&       |
| Or   | \|\|     |

### Pre-defined variables

| Variable | Value             |
|----------|-------------------|
| π \| pi  | 3.141592653589793 |
| e        | 2.718281828459045 |

### Pre-defined functions

| Function                             | Description                                                                                                        |
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| abs(expression)                      | Returns the absolute value of the expression                                                                       |
| sum(expression, ...)                 | Returns the sum of all arguments                                                                                   |
| avg(expression, ...)                 | Returns the average of all arguments                                                                               |
| floor(expression)                    | Rounds the value of the expression down to the nearest integer                                                     |
| ceil(expression)                     | Rounds the value of the expression up to the nearest integer                                                       |
| round(expression)                    | Rounds the value of the expression to the nearest integer in the direction decided by the configured rounding mode |
| min(expression, ...)                 | Returns the value of the smallest argument                                                                         |
| max(expression, ...)                 | Returns the value of the largest argument                                                                          |
| if(condition, trueValue, falseValue) | Returns trueValue if condition is true(condition != 0), otherwise it returns falseValue                            |
| ln(expression)                       | Returns the natural logarithm of the expression                                                                    |
| log(expression, base)                | Returns the logarithm of the expression, if no base if s specified, it defaults to 10                              |

### Examples:

````Kotlin
val result = Expressions()
    .eval("(5+5)*10") // returns 100
````

You can define variables with the `define` method.

````Kotlin
val result = Expressions()
    .define("x", 5)
    .eval("x*10") // returns 50
````

The define method returns the expression instance to allow chaining definition method calls together.

````Kotlin
val result = Expressions()
    .define("x", 5)
    .define("y", "5*2")
    .eval("x*y") // returns 50
````

Variable definition expressions can reference previously defined variables.

````Kotlin
val result = Expressions()
    .define("x", 5)
    .define("y", "x^2")
    .eval("y*x") // returns 125
````

You can add new functions with the `addFunction` method.

````kotlin
val result = Expressions()
    .addFunction("min") { arguments ->
        if (arguments.isEmpty()) throw ExpressionException(
            "min requires at least one argument"
        )

        arguments.min()!!
    }
    .eval("min(4, 8, 16)") // returns 4
````

You can set the precision and rounding mode with `setPrecision` and `setRoundingMode`.

````Kotlin
val result = Expressions()
    .setPrecision(128)
    .setRoundingMode(RoundingMode.UP)
    .eval("222^3/5.5") 
````
