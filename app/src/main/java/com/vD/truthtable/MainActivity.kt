package com.vD.truthtable


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.truthtable.ui.theme.TruthtableTheme

/**
 * The main activity for the application.
 */
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the superclass implementation of onCreate.
        super.onCreate(savedInstanceState)

        // Enables the app to draw its UI content edge-to-edge, behind the system bars (status and navigation).
        enableEdgeToEdge()

        // Sets the Jetpack Compose UI content for this activity.
        setContent {
            // Applies the custom application theme, defining colors, typography, etc.
            TruthtableTheme {
                    // A Column arranges its children vertically.
                    Column(
                        modifier = Modifier
                            // Makes the Column fill the entire available space.
                            .fillMaxSize()
                            // Sets the background color for the entire screen.
                            .background(color = Color(138, 170, 229))
                    ) {
                        // Renders the main screen content of the application.
                        MainScreen()
                    }
                }
            }
        }
    }

