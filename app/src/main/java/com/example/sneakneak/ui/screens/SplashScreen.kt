package com.example.sneakneak.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sneakneak.R
import kotlinx.coroutines.delay

// Splash UI
@Composable
fun SplashScreen(navController: NavController) {

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // Animation Effect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
            )
        )
        delay(1200L)
        navController.navigate("main_screen")
    }

    // Img
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
            .background(color = Color(0xFF48B2E7)),
    ){
        Image(painter = painterResource(id = R.drawable.logo_foreground),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
                .size(200.dp))
    }
}