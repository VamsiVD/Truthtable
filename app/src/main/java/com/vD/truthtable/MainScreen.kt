package com.vD.truthtable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(expressionViewModel: ExpressionViewModel = viewModel()){
    // Retrieves the expression input string from the ViewModel.
    val textInput = expressionViewModel.expressionInput
    // Retrieves the validation error message from the ViewModel, if any.
    val validationError = expressionViewModel.validationErrorMessage
    // Gets the software keyboard controller to manage the keyboard's visibility.
    val keyboardController = LocalSoftwareKeyboardController.current
    // Retrieves the tokenized version of the expression for display.
    val displayedTokens = expressionViewModel.tokens
    // State to control whether the truth table is shown.
    var showTruthTable by remember { mutableStateOf(false) }
    // State to track if the text field has focus.
    var textFieldHasFocus by remember { mutableStateOf(false) }
    // Gets the focus manager to control UI focus.
    val focusManager = LocalFocusManager.current

    // Handles the back button press. If the text field is focused, it clears the focus.
    BackHandler(enabled = textFieldHasFocus) {
        focusManager.clearFocus()
    }

    // Main layout column that fills the screen width and has padding.
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(top = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text field for user to enter the boolean expression.
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
                // Updates the textFieldHasFocus state when focus changes.
                .onFocusChanged { focusState ->
                    textFieldHasFocus = focusState.isFocused
                },
            // Custom colors for different states of the text field (e.g., error, focused).
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = Color.Red,
                errorCursorColor = Color.Red,
                errorLabelColor = Color.Red,
                errorLeadingIconColor = Color.Red,
                errorTrailingIconColor = Color.Red,
                errorContainerColor = Color(16, 24, 32),
                errorTextColor = Color(138, 170, 229),
                focusedLabelColor = Color(16, 24, 32),
                unfocusedLabelColor = if (textInput.isNotEmpty()) Color(16, 24, 32) else Color(138, 170, 229),
                unfocusedContainerColor = Color(16, 24, 32),
                focusedContainerColor = Color(16, 24, 32),
                unfocusedTextColor = Color(138, 170, 229),
                focusedTextColor = Color(138, 170, 229),
            ),


            // The current value of the text field.
            value = textInput.uppercase(),
            // Callback that updates the ViewModel when the text changes.
            onValueChange = { expressionViewModel.onExpressionChange(it) },
            // The label displayed inside the text field.
            label = { Text(text = "Enter the expression") },
            // An icon at the end of the text field.
            trailingIcon = {
                // Shows a clear button only if the text input is not empty.
                if (textInput.isNotEmpty()) {
                    IconButton(onClick = {
                        // Hides the truth table and clears the expression.
                        showTruthTable = false
                        expressionViewModel.onExpressionChange("")
                    }) {
                        Icon(
                            tint = Color(138, 170, 229),
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
            // Defines the shape of the text field's border.
            shape = RoundedCornerShape(18.dp),
            // Configures the keyboard options, setting the action button to "Search".
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            // Defines actions to be taken when keyboard actions are triggered.
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Hides the keyboard.
                    keyboardController?.hide()
                    // Processes the expression in the ViewModel.
                    expressionViewModel.processExpressionAndDisplay()
                    // Sets the state to show the truth table.
                    showTruthTable = true
                }
            ),
            // Restricts the text field to a single line.
            singleLine = true,
            // Marks the field as erroneous if there is a validation error.
            isError = validationError != null
        )
        // Displays the validation error message if it exists.
        validationError?.let {
            Text(
                color = Color(16, 24, 32),
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Conditionally displays the TruthTable composable.
    if (showTruthTable && validationError == null && displayedTokens?.isNotEmpty() == true) {
        TruthTable()
    }

    // Conditionally displays the RulesSection composable.
    if (!showTruthTable || validationError != null || displayedTokens?.isEmpty() == true) {
        RulesSection()
    }

}

@Composable
fun RulesSection() {
    // A card composable to provide a styled container for the rules.
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        // A column to arrange the rule texts vertically.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(16, 24, 32))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title for the rules section.
            Text(
                text = "Expression Rules",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(138, 170, 229),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            // A column to align the rules to the start.
            Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start){
                // A list of strings containing the expression rules.
                val rules = listOf(
                    " • Use variables: A, B, C, etc.",
                    " • AND operator: * ",
                    " • OR operator: + ",
                    " • NOT operator: ' ",
                    " • XOR operator: ^ ",
                    " • Use parentheses for grouping: (A * B)",
                    " • Example: (A * B)' + (C')"
                )

                // Iterates over the rules list and creates a Text composable for each rule.
                rules.forEach { rule ->
                    Text(
                        text = rule,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(138, 170, 229),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TruthTable(
    expressionViewModel: ExpressionViewModel = viewModel()
) {
    // Retrieves the list of variables from the ViewModel.
    val variables = expressionViewModel.variables ?: emptyList()
    // Retrieves the rows of the truth table from the ViewModel.
    val truthTableRows = expressionViewModel.truthTableRows
    // Combines variables with "Result" to create the table headers.
    val allHeaders = variables + "Result"
    // Gets the current screen width.
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    // Calculates the number of columns in the table.
    val columnCount = allHeaders.size
    // Calculates the width for each cell to fit the screen.
    val dynamicCellWidth = (screenWidth - 32.dp) / columnCount

    // Modifier for styling each cell in the table (width, border, padding).
    val cellModifier = Modifier
        .width(dynamicCellWidth)
        .border(1.dp, Color.Black)
        .padding(8.dp)

    // State to remember the horizontal scroll position.
    val horizontalScroll = rememberScrollState()

    // A card container for the truth table.
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // A column to structure the header and data rows.
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row of the table, which is horizontally scrollable.
            Row(modifier = Modifier
                .horizontalScroll(horizontalScroll)
                .background(Color(16, 24, 32))) {
                // Creates a Text composable for each header.
                allHeaders.forEach { header ->
                    Text(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        text = header,
                        modifier = cellModifier,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // A lazy column to efficiently display the data rows, especially for long tables.
            LazyColumn(
                modifier = Modifier.fillMaxWidth().background(Color(56,52,60)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Creates a row for each item in the truthTableRows list.
                items(truthTableRows.size) { rowIndex ->
                    val row = truthTableRows[rowIndex]

                    // A horizontally scrollable row for the table data.
                    Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        // Creates a Text composable for each cell in the row.
                        row.forEach { cell ->
                            Text(
                                textAlign = TextAlign.Center,
                                text = cell,
                                modifier = cellModifier,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}