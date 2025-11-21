package com.example.aulalink.views

import androidx.compose.runtime.Composable

import com.example.aulalink.data.models.Curso
import com.example.aulalink.models.UserProfile
import com.example.aulalink.views.CourseListScreen

@Composable
fun HomeScreen(
    user: UserProfile,
    cursos: List<Curso>,
    onNavigateCurso: (String) -> Unit
) {
    CourseListScreen(
        cursos = cursos,
        esTutor = user.role == "tutor",
        onCursoClick = { curso -> onNavigateCurso(curso.id) }
    )
}
