package com.example.aulalink.views.sesion

import androidx.compose.runtime.*
import com.example.aulalink.models.UserProfile
import com.example.aulalink.views.LoginView
import com.example.aulalink.views.RegisterView

@Composable
fun AuthScreen(
    onLoginSuccess: (UserProfile) -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }

    if (isLogin) {
        LoginView(
            onLoginSuccess = { userProfile: UserProfile ->
                onLoginSuccess(userProfile)
            },
            onSwitchToRegister = { isLogin = false }
        )
    } else {
        RegisterView(
            onRegisterSuccess = { userProfile: UserProfile ->
                isLogin = true
                onLoginSuccess(userProfile)
            },
            onSwitchToLogin = { isLogin = true }
        )
    }
}
