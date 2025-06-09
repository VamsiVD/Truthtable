package com.vD.truthtable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import BooleanExpressionHelper
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

/**
 * ViewModel for managing the state and logic of the boolean expression and its truth table.
 * It holds the user input, validation status, and the resulting truth table data.
 */
class ExpressionViewModel : ViewModel() {

    // The raw expression string entered by the user. UI observes this for changes.
    // The `private set` ensures it can only be modified within this ViewModel.
    var expressionInput by mutableStateOf("")
        private set

    // Holds an error message if the expression is invalid. Null if the expression is valid.
    var validationErrorMessage by mutableStateOf<String?>(null)
        private set

    // The expression broken down into a list of its parts (e.g., "A", "+", "B").
    var tokens by mutableStateOf<List<String>?>(null)
        private set

    // Stores the last expression that was successfully processed to avoid re-computation.
    var lastSuccessfullyProcessedInput by mutableStateOf<String?>(null)
        private set

    // If the expression is invalid, this list holds the specific parts that are incorrect.
    var invalidTokens by mutableStateOf<List<String>?>(null)
        private set

    // A list of unique variable names (e.g., "A", "B") extracted from the expression.
    var variables by mutableStateOf<List<String>?>(null)
        private set

    // The expression in Reverse Polish Notation (postfix), ready for evaluation.
    var postfix by mutableStateOf<List<String>?>(null)
        private set

    // A list of lists, where each inner list represents a single row in the truth table.
    // Using `mutableStateListOf` is efficient for Compose when list items are added/removed.
    var truthTableRows = mutableStateListOf<List<String>>()
        private set


    /**
     * Called whenever the user changes the text in the input field.
     * @param newExpression The updated expression string from the UI.
     */
    fun onExpressionChange(newExpression: String) {
        // Update the current expression.
        expressionInput = newExpression
        // Reset all derived state and errors since the input has changed.
        validationErrorMessage = null
        tokens = emptyList()
        lastSuccessfullyProcessedInput = null
        truthTableRows.clear()
    }

    /**
     * Processes the current expression input to validate it, tokenize it,
     * and generate the corresponding truth table.
     */
    fun processExpressionAndDisplay() {
        // Use the helper class to analyze the expression.
        val helper = BooleanExpressionHelper(expressionInput)

        // Check if the expression has a valid format.
        if (helper.isValid()) {
            validationErrorMessage = null // Clear any old errors.
            val generatedTokens = helper.tokenize() // Break the expression into tokens.

            if (generatedTokens != null) {
                // If tokenization is successful, proceed with processing.
                tokens = generatedTokens
                // Convert tokens to postfix notation using the Shunting-Yard algorithm.
                postfix = ShuntingYard().toPostfix(generatedTokens)
                // Extract unique variables from the expression.
                variables = helper.getVariables()
                // Save the successfully processed input.
                lastSuccessfullyProcessedInput = expressionInput
                // Generate the truth table rows.
                generateTruthTable()
            } else {
                // Handle the rare case where validation passes but tokenization fails.
                validationErrorMessage = "Expression is valid, but tokenization failed."
                tokens = emptyList()
                lastSuccessfullyProcessedInput = null
                truthTableRows.clear()
            }
        } else {
            // If the expression is not valid, create an error message.
            var combinedErrorMessage = ""
            invalidTokens = helper.inValidTokens() // Get the specific invalid parts.
            if (!invalidTokens.isNullOrEmpty()) {
                // Add the invalid characters to the error message for clarity.
                combinedErrorMessage += "Invalid Characters: ${invalidTokens?.joinToString(", ")}\n"
            }
            combinedErrorMessage += "Please check the format."

            // Set the error message and clear any old data.
            validationErrorMessage = combinedErrorMessage
            tokens = emptyList()
            lastSuccessfullyProcessedInput = null
            truthTableRows.clear()
        }
    }

    /**
     * Generates all rows for the truth table based on the current variables and postfix expression.
     */
    private fun generateTruthTable() {
        truthTableRows.clear() // Clear previous results.
        val vars = variables ?: return // Exit if there are no variables.
        // Generate all possible true/false combinations for the variables.
        val combinations = generateTruthCombinations(vars)

        // Iterate through each combination of variable assignments.
        for (assignment in combinations) {
            // Convert boolean values to integers (1 for true, 0 for false).
            val intVars = assignment.mapValues { if (it.value) 1 else 0 }
            // Evaluate the postfix expression with the current variable assignments.
            val result = evaluatePostfix(intVars) ?: 0
            // Create a row with the variable values and the final result.
            val row = vars.map { if (assignment[it] == true) "1" else "0" } + result.toString()
            truthTableRows.add(row) // Add the new row to the state list.
        }
    }

    /**
     * Generates all 2^n possible truth combinations for a given list of variables.
     * @param variables The list of variable names.
     * @return A list of maps, where each map represents one combination of variable assignments.
     */
    internal fun generateTruthCombinations(variables: List<String>): List<Map<String, Boolean>> {
        val total = 1 shl variables.size // Calculate 2 to the power of number of variables.
        return List(total) { index ->
            // For each combination index, determine the boolean value for each variable.
            variables.mapIndexed { i, name ->
                // Use bitwise operations to determine if the i-th bit is 1 or 0.
                name to ((index shr (variables.size - i - 1)) and 1 == 1)
            }.toMap()
        }
    }

    /**
     * Evaluates a postfix expression for a given set of variable values.
     * @param variables A map of variable names to their integer values (0 or 1).
     * @return The integer result (0 or 1) of the expression, or null if an error occurs.
     */
    internal fun evaluatePostfix(variables: Map<String, Int>): Int? {
        val postfix = this.postfix ?: return null // Exit if postfix is not set.
        val stack = ArrayDeque<Int>() // Use a stack for evaluation.

        for (token in postfix) {
            when {
                // If the token is a variable, push its value onto the stack.
                variables.containsKey(token) -> {
                    val value = variables[token.uppercase()]
                    if (value == null) {
                        println("Error: Undefined variable '$token'")
                        return null
                    }
                    stack.addLast(value)
                }
                // If the token is a NOT operator.
                token == "'" -> {
                    if (stack.isEmpty()) return null // Not enough operands.
                    val operand = stack.removeLast()
                    stack.addLast(operand xor 1) // Apply logical NOT (0->1, 1->0).
                }
                // If the token is a binary operator.
                token in listOf("+", "*", "^") -> {
                    if (stack.size < 2) return null // Not enough operands.
                    val right = stack.removeLast()
                    val left = stack.removeLast()
                    val result = when (token) {
                        "+" -> left or right  // OR
                        "*" -> left and right // AND
                        "^" -> left xor right // XOR
                        else -> return null // Should not happen.
                    }
                    stack.addLast(result)
                }
                // If the token is unrecognized.
                else -> return null
            }
        }

        // The final result should be the only item left on the stack.
        return if (stack.size == 1) stack.first() else null
    }

}