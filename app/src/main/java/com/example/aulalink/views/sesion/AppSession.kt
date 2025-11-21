package com.example.aulalink.views.sesion

import com.example.aulalink.models.UserProfile


object AppSession {
    var user: UserProfile? = null
    fun isAuthenticated(): Boolean = user != null
    fun login(userProfile: UserProfile) { user = userProfile }
    fun logout() { user = null }
}
