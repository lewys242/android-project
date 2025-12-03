package com.mbongo.app.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class User(
    val email: String,
    val name: String = "",
    val token: String
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("mbongo_prefs", Context.MODE_PRIVATE)
    
    private val _isLoggedIn = MutableStateFlow(checkIfLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(getCurrentUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private fun checkIfLoggedIn(): Boolean {
        return prefs.getString("mbongo_token", null) != null
    }
    
    private fun getCurrentUser(): User? {
        val token = prefs.getString("mbongo_token", null) ?: return null
        val email = prefs.getString("mbongo_email", "") ?: ""
        val name = prefs.getString("mbongo_name", "") ?: ""
        return User(email, name, token)
    }
    
    fun login(email: String, password: String, name: String = "") {
        // Créer un token simple (comme dans l'app web)
        val token = android.util.Base64.encodeToString(
            "$email:$password".toByteArray(),
            android.util.Base64.NO_WRAP
        )
        
        // Sauvegarder dans SharedPreferences
        prefs.edit().apply {
            putString("mbongo_token", token)
            putString("mbongo_email", email)
            putString("mbongo_name", name)
            apply()
        }
        
        _currentUser.value = User(email, name, token)
        _isLoggedIn.value = true
    }
    
    fun logout() {
        prefs.edit().apply {
            remove("mbongo_token")
            remove("mbongo_email")
            remove("mbongo_name")
            apply()
        }
        
        _currentUser.value = null
        _isLoggedIn.value = false
    }
}
