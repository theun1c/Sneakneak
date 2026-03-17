package com.example.sneakneak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.sneakneak.ui.navigation.Navigation
import com.example.sneakneak.ui.theme.AppTheme

// Single-activity host for the Compose UI tree.
// At the current UI stage the activity is intentionally thin:
// theme + navigation host are the only entry points.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                ) {
                    Navigation()
                }
            }
        }
    }
}

