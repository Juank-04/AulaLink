package com.example.aulalink.views

import androidx.compose.runtime.*

@Composable
fun AuthScreen(
    onAuthSuccess: (UserProfile) -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }

    if (isLogin) {
        LoginView(
            onLoginSuccess = onAuthSuccess,
            onSwitchToRegister = { isLogin = false }
        )
    } else {
        RegisterView(
            onRegisterSuccess = onAuthSuccess,
            onSwitchToLogin = { isLogin = true }
        )
    }
}
