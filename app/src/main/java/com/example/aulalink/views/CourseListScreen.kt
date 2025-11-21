package com.example.aulalink.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulalink.data.models.Curso

@Composable
fun CourseListScreen(
    cursos: List<Curso>,
    esTutor: Boolean,
    onCursoClick: (Curso) -> Unit
) {
    Column {
        cursos.forEach { curso ->
            CursoCard(
                curso = curso,
                esTutor = esTutor,
                onClick = { onCursoClick(curso) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CursoCard(
    curso: Curso,
    esTutor: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = curso.nombre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (esTutor) "Tutor" else "Estudiante",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
