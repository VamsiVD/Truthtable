package com.vD.truthtable

import android.util.Log

/**
 * Implements the Shunting-yard algorithm to convert a list of tokens
 * from infix notation (e.g., "A + B") to postfix notation (e.g., "A B +").
 * This is a crucial step before evaluating the expression.
 */
class ShuntingYard() {
    // This queue stores the final postfix expression as it's being built.
    private val outputQueue = mutableListOf<String>()
    // This stack holds operators temporarily as they are being sorted by precedence.
    val operators = ArrayDeque<String>()

    // Defines the precedence for each operator. Higher numbers mean higher precedence.
    private val operatorPrecedence = mapOf(
        "'" to 4,   // NOT (has the highest precedence)
        "*" to 3,   // AND
        "^" to 2,   // XOR
        "+" to 1    // OR (has the lowest precedence)
    )

    /**
     * Returns the precedence of a given operator.
     * @param op The operator string.
     * @return The integer precedence value, or 0 if not found.
     */
    private fun precedence(op: String): Int {
        return operatorPrecedence[op] ?: 0
    }

    /**
     * Checks if an operator is right-associative.
     * In boolean logic, only unary operators like NOT are typically handled this way.
     * @param op The operator string.
     * @return True if the operator is right-associative, false otherwise.
     */
    private fun isRightAssociative(op: String): Boolean {
        // In this implementation, only the NOT operator is considered right-associative.
        return op == "\'"
    }

    // A set for quick lookups to identify if a token is an operator or parenthesis.
    private val knownOperatorsAndParentheses = setOf("'", "*", "^", "+", "(", ")")

    /**
     * Converts a list of infix tokens to a postfix list of tokens.
     * @param tokens The infix expression, already tokenized into a list.
     * @return A list of tokens in postfix order, or null if an error occurred (e.g., mismatched parentheses).
     */
    fun toPostfix(tokens: List<String>?): List<String>? {
        outputQueue.clear() // Reset the output queue for this run.
        operators.clear()   // Reset the operator stack for this run.

        // If the input list is null, we cannot proceed.
        if (tokens == null) {
            println("Input tokens list is null.")
            return null
        }

        // Process each token from the input list.
        for (token in tokens) {
            when {
                // Case 1: The token is an operand (a variable).
                !knownOperatorsAndParentheses.contains(token) -> {
                    // Operands are immediately added to the output queue.
                    outputQueue.add(token)
                }

                // Case 2: The token is the unary NOT operator.
                token == "'" -> {
                    // Before pushing the operator, pop operators with higher or equal precedence from the stack.
                    while (
                        operators.isNotEmpty() &&
                        operators.first() != "(" &&
                        (precedence(operators.first()) > precedence(token) ||
                                (precedence(operators.first()) == precedence(token) && !isRightAssociative(token)))
                    ) {
                        outputQueue.add(operators.removeFirst())
                    }
                    // Push the current operator onto the stack.
                    operators.addFirst(token)
                }

                // Case 3: The token is a binary operator (+, *, ^).
                token in listOf("+", "*", "^") -> {
                    // While there's an operator on the stack with higher or equal precedence, pop it.
                    while (
                        operators.isNotEmpty() &&
                        operators.first() != "(" &&
                        (
                                precedence(operators.first()) > precedence(token) ||
                                        (precedence(operators.first()) == precedence(token) && !isRightAssociative(token))
                                )
                    ) {
                        outputQueue.add(operators.removeFirst())
                    }
                    // Push the current operator onto the stack.
                    operators.addFirst(token)
                }

                // Case 4: The token is a left parenthesis.
                token == "(" -> {
                    // Left parentheses are always pushed onto the operator stack.
                    operators.addFirst(token)
                }

                // Case 5: The token is a right parenthesis.
                token == ")" -> {
                    // Pop operators from the stack to the output until a left parenthesis is found.
                    while (operators.isNotEmpty() && operators.first() != "(") {
                        outputQueue.add(operators.removeFirst())
                    }
                    // If the stack becomes empty, there was no matching left parenthesis.
                    if (operators.isEmpty()) {
                        Log.e("ShuntingYard", "Mismatched parentheses: No matching '('.")
                        return null // Error: Mismatched parentheses.
                    }
                    // Pop and discard the left parenthesis.
                    operators.removeFirst()
                }

                // Case 6: The token is unknown.
                else -> {
                    // This should not be reached if the tokenizer and known operators are correct.
                    Log.e("ShuntingYard", "Invalid or unknown token in input: $token")
                    return null
                }
            }
        }

        // After processing all tokens, pop any remaining operators from the stack to the output.
        while (operators.isNotEmpty()) {
            val operator = operators.first()
            // If a left parenthesis is found here, it means there was a mismatched parenthesis.
            if (operator == "(") {
                Log.e("ShuntingYard", "Mismatched parentheses: '(' found at end of processing.")
                return null // Error: Mismatched parentheses.
            }
            outputQueue.add(operators.removeFirst())
        }

        Log.d("ShuntingYard", "Output Queue: $outputQueue")
        // Return an immutable copy of the output queue.
        return outputQueue.toList()
    }
}