package com.example.aulalink.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aulalink.views.HomeScreen
import com.example.aulalink.views.CourseDetailScreenNavWrapper

import com.example.aulalink.data.models.Curso
import com.example.aulalink.models.UserProfile
import com.example.aulalink.views.sesion.AuthScreen


@Composable
fun AppNavHost(
    navController: NavHostController
) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    val cursos = listOf(
        Curso(id = "algebra", nombre = "Álgebra", descripcion = "Curso de álgebra básica"),
        Curso(id = "fisica", nombre = "Física", descripcion = "Introducción a la física"),
        Curso(id = "ingles", nombre = "Inglés", descripcion = "Fundamentos de inglés")
    )

    NavHost(
        navController = navController,
        startDestination = if (userProfile == null) "login" else "home"
    ) {
        composable("login") {
            AuthScreen(
                onLoginSuccess = { profile ->
                    userProfile = profile
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            userProfile?.let { user ->
                HomeScreen(
                    user = user,
                    cursos = cursos,
                    onNavigateCurso = { cursoId ->
                        navController.navigate("cursoDetail/$cursoId")
                    }
                )
            }
        }
        composable("cursoDetail/{cursoId}") { backStackEntry ->
            val cursoId = backStackEntry.arguments?.getString("cursoId") ?: ""
            userProfile?.let { user ->
                CourseDetailScreenNavWrapper(
                    cursoId = cursoId,
                    user = user,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
