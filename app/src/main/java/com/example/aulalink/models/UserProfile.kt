package com.example.aulalink.models

data class UserProfile(
    val nombre: String = "",
    val email: String = "",
    val role: String = "" // "tutor" o "estudiante"
)
