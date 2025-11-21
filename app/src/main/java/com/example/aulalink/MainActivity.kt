package com.example.aulalink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.aulalink.navigation.AppNavHost
import com.example.aulalink.ui.theme.AulaLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AulaLinkTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}
