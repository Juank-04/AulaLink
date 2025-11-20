package com.example.aulalink.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit = {}
) {
    var isLogin by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLogin) {
            LoginView(
                onLoginSuccess = onAuthSuccess,
                onSwitchToRegister = { isLogin = false }
            )
        } else {
            RegisterView(
                onRegisterSuccess = {
                    isLogin = true
                    onAuthSuccess()
                },
                onSwitchToLogin = { isLogin = true }
            )
        }
    }
}