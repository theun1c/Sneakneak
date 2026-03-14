package com.example.sneakneak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sneakneak.ui.navigation.Navigation
import com.example.sneakneak.ui.theme.SneakneakTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SneakneakTheme {
                Surface(color = Color.White, modifier = Modifier.fillMaxSize()){
                    Navigation()
                }
            }
        }
    }
}



