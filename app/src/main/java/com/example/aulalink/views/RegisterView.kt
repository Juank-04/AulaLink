package com.example.aulalink.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulalink.models.UserProfile
import com.example.aulalink.ui.theme.*
import com.example.aulalink.views.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

@Composable
fun RegisterView(
    onRegisterSuccess: (UserProfile) -> Unit = {},
    onSwitchToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("estudiante") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground, Color(0xFF14141F))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurface.copy(alpha = 0.92f)
            ),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScreenHeader(
                    title = "Crear cuenta",
                    subtitle = "Únete como estudiante o tutor."
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AccentRed
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentRed,
                        unfocusedBorderColor = Color(0xFF3A3A4A),
                        cursorColor = AccentRed,
                        focusedLabelColor = AccentRed,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = AccentRed
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentRed,
                        unfocusedBorderColor = Color(0xFF3A3A4A),
                        cursorColor = AccentRed,
                        focusedLabelColor = AccentRed,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AccentRed
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentRed,
                        unfocusedBorderColor = Color(0xFF3A3A4A),
                        cursorColor = AccentRed,
                        focusedLabelColor = AccentRed,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Tipo de usuario
                Text(
                    text = "Tipo de usuario",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = role == "estudiante",
                            onClick = { role = "estudiante" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AccentRed,
                                unselectedColor = TextSecondary
                            )
                        )
                        Text("Estudiante", color = TextPrimary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = role == "tutor",
                            onClick = { role = "tutor" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AccentRed,
                                unselectedColor = TextSecondary
                            )
                        )
                        Text("Tutor", color = TextPrimary)
                    }
                }

                Button(
                    onClick = {
                        isLoading = true
                        errorMsg = null
                        auth.createUserWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid
                                    if (uid != null) {
                                        val userData = hashMapOf(
                                            "nombre" to nombre,
                                            "email" to email.trim(),
                                            "role" to role
                                        )

                                        db.collection("users")
                                            .document(uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                onRegisterSuccess(
                                                    UserProfile(
                                                        nombre = nombre,
                                                        email = email.trim(),
                                                        role = role
                                                    )
                                                )
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                errorMsg = when (e) {
                                                    is FirebaseFirestoreException ->
                                                        "No se pudo guardar el usuario en la base de datos."
                                                    else ->
                                                        "Ocurrió un error al guardar los datos."
                                                }
                                            }
                                    } else {
                                        isLoading = false
                                        errorMsg = "No se pudo obtener el usuario."
                                    }
                                } else {
                                    isLoading = false
                                    val e = task.exception
                                    errorMsg = when (e) {
                                        is FirebaseAuthException -> when (e.errorCode) {
                                            "ERROR_EMAIL_ALREADY_IN_USE" ->
                                                "Este correo ya está registrado."
                                            "ERROR_INVALID_EMAIL" ->
                                                "El correo no es válido."
                                            "ERROR_WEAK_PASSWORD" ->
                                                "La contraseña es demasiado débil."
                                            else ->
                                                "No se pudo crear la cuenta. Inténtalo de nuevo."
                                        }
                                        else -> "Ocurrió un error inesperado."
                                    }
                                }
                            }
                    },
                    enabled = nombre.isNotBlank() &&
                            email.isNotBlank() &&
                            password.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "Crear cuenta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                TextButton(
                    onClick = onSwitchToLogin,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = TextSecondary
                    )
                }

                errorMsg?.let { msg ->
                    Text(
                        text = msg,
                        color = AccentRed,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
