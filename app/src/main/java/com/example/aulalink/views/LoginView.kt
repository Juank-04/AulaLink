package com.example.aulalink.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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

@Composable
fun LoginView(
    onLoginSuccess: (UserProfile) -> Unit = {},
    onSwitchToRegister: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                    title = "Iniciar sesión",
                    subtitle = "Accede a tus cursos y materiales."
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

                Button(
                    onClick = {
                        isLoading = true
                        errorMsg = null

                        auth.signInWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid
                                    if (uid != null) {
                                        db.collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnSuccessListener { doc ->
                                                isLoading = false
                                                val user = doc.toObject(UserProfile::class.java)
                                                if (user != null) {
                                                    onLoginSuccess(user)
                                                } else {
                                                    errorMsg =
                                                        "No se encontraron tus datos en la base de datos."
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                errorMsg = e.localizedMessage
                                                    ?: "No se pudieron cargar tus datos."
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
                                            "ERROR_INVALID_EMAIL" ->
                                                "El correo no es válido."
                                            "ERROR_USER_NOT_FOUND" ->
                                                "No existe una cuenta con este correo."
                                            "ERROR_WRONG_PASSWORD" ->
                                                "La contraseña es incorrecta."
                                            "ERROR_USER_DISABLED" ->
                                                "Esta cuenta ha sido deshabilitada."
                                            else ->
                                                "Ocurrió un error al iniciar sesión. Inténtalo de nuevo."
                                        }
                                        else -> "Ocurrió un error inesperado."
                                    }
                                }
                            }
                    },
                    enabled = email.isNotBlank() && password.isNotBlank(),
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
                            text = "Iniciar sesión",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.dp.value.sp
                        )
                    }
                }

                TextButton(
                    onClick = onSwitchToRegister,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
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
