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
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Rol que luego usarás para restringir vistas
    var role by remember { mutableStateOf("estudiante") }

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
                text = "Crear cuenta",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
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

            Text(
                text = "Tipo de usuario",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                            selectedColor = Color.Red,
                            unselectedColor = Color.White
                        )
                    )
                    Text(text = "Estudiante", color = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "tutor",
                        onClick = { role = "tutor" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Red,
                            unselectedColor = Color.White
                        )
                    )
                    Text(text = "Tutor", color = Color.White)
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
                        text = "Crear cuenta",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = onSwitchToLogin,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
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
