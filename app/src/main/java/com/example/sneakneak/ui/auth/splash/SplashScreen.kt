package com.example.sneakneak.ui.auth.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.auth.usecase.ObserveSessionUseCase
import com.example.sneakneak.R
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.delay

// Splash currently doubles as a lightweight session gate for the UI stage.
data class SplashUiState(
    val isAnimating: Boolean = true,
)

sealed interface SplashUiEvent {
    data object Started : SplashUiEvent
}

sealed interface SplashUiEffect {
    data object NavigateToSignIn : SplashUiEffect
    data object NavigateToHome : SplashUiEffect
}

class SplashViewModel(
    private val observeSessionUseCase: ObserveSessionUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(SplashUiState())
        private set

    var uiEffect by mutableStateOf<SplashUiEffect?>(null)
        private set

    fun onEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.Started -> {
                // TODO(DATA): once session observation becomes asynchronous/reactive, keep the same
                // effect contract but source it from real auth state instead of fake memory state.
                uiEffect = if (observeSessionUseCase().value == null) {
                    SplashUiEffect.NavigateToSignIn
                } else {
                    SplashUiEffect.NavigateToHome
                }
            }
        }
    }

    fun consumeEffect() {
        uiEffect = null
    }
}

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = remember { SplashViewModel(AppContainer.authUseCases.observeSession) },
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(4f).getInterpolation(it) },
            ),
        )
        delay(1200L)
        viewModel.onEvent(SplashUiEvent.Started)
    }

    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            SplashUiEffect.NavigateToHome -> {
                onNavigateToHome()
                viewModel.consumeEffect()
            }

            SplashUiEffect.NavigateToSignIn -> {
                onNavigateToSignIn()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    SplashScreen(
        state = viewModel.uiState,
        scale = scale.value,
    )
}

@Composable
fun SplashScreen(
    state: SplashUiState,
    scale: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .scale(if (state.isAnimating) scale else 0.7f)
                .size(200.dp),
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    AppTheme {
        SplashScreen(
            state = SplashUiState(),
            scale = 0.7f,
        )
    }
}
