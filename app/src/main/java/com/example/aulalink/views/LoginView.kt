package com.example.aulalink.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "AulaLink",
                color = Color.Red,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Iniciar sesión",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
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
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
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
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = onSwitchToRegister,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = Color.White
                )
            }

            errorMsg?.let { msg ->
                Text(
                    text = msg,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
