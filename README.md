# Truth Table Generator - Android App

## Overview

Truth Table Generator is an Android application built with Jetpack Compose that allows users to input boolean logic expressions and generate their corresponding truth tables. It provides a simple interface for entering expressions, validates the input, and clearly displays the resulting truth table. The app also includes a handy reference for the supported operators and expression syntax.

## Features

*   **Boolean Expression Input:** Enter complex boolean expressions using variables (e.g., A, B, C) and standard logical operators.
*   **Syntax Validation:** The app checks the expression for syntax errors and provides feedback to the user.
*   **Truth Table Generation:** Automatically generates and displays a complete truth table for the valid expression, showing all possible combinations of variable states and the resulting output.
*   **Supported Operators:**
    *   **AND:** `*`
    *   **OR:** `+`
    *   **NOT:** `'` (e.g., `A'`)
    *   **XOR:** `^`
*   **Parentheses for Grouping:** Use `()` to define the order of operations.
*   **Rules and Examples:** A clear section outlining the rules for writing expressions and providing examples.
*   **Clear UI:** Built with Material 3 components in Jetpack Compose for a modern and responsive user experience.
*   **Dynamic Table Sizing:** The truth table columns adjust to fit the screen width.

## Screenshots
![Screenshot_20250606_145030](https://github.com/user-attachments/assets/0957b41d-3a0a-42a4-b4f3-7bf2ef5a1378)
![Screenshot_20250606_145149](https://github.com/user-attachments/assets/e54dd6b7-aaed-4b05-8e2a-4b130a06f0e9)

## How to Use

1.  **Enter Expression:** Type your boolean logic expression into the input field.
    *   Use uppercase letters for variables (A, B, C, ...).
    *   Use the supported operators: `*` (AND), `+` (OR), `'` (NOT), `^` (XOR).
    *   Use parentheses `()` for grouping.
    *   Example: `(A * B)' + C'`
2.  **Generate Table:** Press the "Search" (or "Enter") key on your keyboard.
3.  **View Results:**
    *   If the expression is valid, the truth table will be displayed below the input field.
    *   If there are errors in the expression, an error message will appear, guiding you to correct it.
4.  **Clear Input:** Use the clear button (X icon) in the input field to erase the current expression.
5.  **View Rules:** If the truth table is not visible (or if there's an error), the "Expression Rules" section will be shown, providing syntax guidance.

