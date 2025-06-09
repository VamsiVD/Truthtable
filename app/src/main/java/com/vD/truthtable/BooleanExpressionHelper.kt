/**
 * A helper class designed to validate, parse, and extract information
 * from a boolean expression provided as a string.
 * @param regInput The raw string input representing the boolean expression. It can be null.
 */
class BooleanExpressionHelper(private val regInput: String?) {

    /**
     * A regular expression for validating the overall structure of the boolean expression.
     * It checks for correct syntax, including variable names, operators (+, *, ^),
     * parentheses for grouping, and the NOT operator (').
     */
    private val originalValidationRegex = Regex(
        "^(([A-Za-z][A-Za-z0-9]*|\\([A-Za-z0-9'*+^()]+\\))'?([+*^]([A-Za-z][A-Za-z0-9]*|\\([A-Za-z0-9'*+^()]+\\))'?)*)$"
    )

    /**
     * A lazily initialized property that holds the input string with all whitespace removed.
     * It is only computed once when first accessed. If the initial regInput is null, it defaults to an empty string.
     */
    private val processedInput: String by lazy {
        regInput?.filter { !it.isWhitespace() } ?: ""
    }

    /**
     * Checks if the input expression is valid.
     * @return `true` if the expression is not null, not empty, and matches the validation regex; `false` otherwise.
     */
    fun isValid(): Boolean {
        // An expression cannot be valid if the input is null.
        if (regInput == null) return false
        // An empty expression is not considered valid.
        if (processedInput.isEmpty()) return false
        // The processed input must match the predefined regex for a valid expression.
        return originalValidationRegex.matches(processedInput)
    }

    /**
     * Attempts to find parts of the expression that are not valid tokens (e.g., operators, invalid characters).
     * It works by splitting the string by what it considers valid variable tokens.
     * @return A list of invalid tokens found, or null if all parts are considered valid tokens.
     */
    fun inValidTokens(): List<String>? {
        // This regex defines what a valid standalone token (like a variable) looks like.
        val tokenRegex = Regex("[^A-Za-z0-9'+*^()]+")
        // Split the expression by the valid tokens, the remaining parts are the invalid ones.
        val invalidTokens = tokenRegex.findAll(processedInput)
            .map { it.value }
            .filter { it.isNotEmpty() }
            .toList()
        // If the list of invalid tokens is empty, it means no invalid parts were found.
        return if (invalidTokens.isEmpty()) null else invalidTokens
    }

    /**
     * Extracts all unique variable names from the expression.
     * For example, in "(A' * B) + A", it would return ["A", "B"].
     * @return A list of unique, base variable names (with any trailing ' removed), or null if tokenization fails.
     */
    fun getVariables(): List<String>? {
        // First, break the expression into its component parts (tokens).
        val tokens = tokenize()
        // This regex identifies valid variables (an uppercase letter followed by optional letters/numbers, and an optional trailing ').
        val variableRegex = Regex("^[A-Z][A-Z0-9]*'?$")

        // Filter the tokens to keep only those that match the variable regex.
        val variables = tokens
            ?.filter { variableRegex.matches(it) }
            ?.distinct() // Keep only the unique variable names (e.g., A and A' are treated as distinct for now).

        // Remove the NOT operator (') from the end of each variable to get the base name.
        val baseVariables = variables?.map { it.removeSuffix("'") }

        return baseVariables
    }

    /**
     * Breaks down the processed input string into a list of tokens.
     * Tokens can be variables, operators, or parentheses.
     * For example, "(A'+B)" becomes ["(", "A'", "+", "B", ")"].
     * @return A list of string tokens, or null if tokenization fails on a non-empty string.
     */
    fun tokenize(): List<String>? {
        // This regex finds all occurrences of multi-character words, ', (, ), +, *, or ^.
        val tokenRegex = Regex("([A-Za-z0-9]+|\\'|\\(|\\)|\\+|\\*|\\^)")

        // A mutable list to store the found tokens.
        val tokens = mutableListOf<String>()

        // Find all matches for the token regex in the input and add them to the list.
        tokenRegex.findAll(processedInput).forEach { matchResult ->
            tokens.add(matchResult.value.uppercase())
        }

        // If tokenization produced no tokens for a non-empty string, it's a failure.
        if (tokens.isEmpty() && processedInput.isNotEmpty()) {
            println("Tokenization failed to produce tokens for: '$processedInput'")
            return null
        }

        // Return the list of tokens, ensuring it's not empty if the input wasn't.
        return tokens.takeIf { it.isNotEmpty() || processedInput.isEmpty() }
    }
}