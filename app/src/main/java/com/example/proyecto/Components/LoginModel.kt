package com.example.proyecto.Components

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val email:String = "",
    val password:String = "",
    val emailError:String = "",
    val passwordError:String = "",
    val phone: String = "",
    val phoneError: String = "",
    val name: String = "",
    val nameError: String = "",
    val photoUrl: String = ""
)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    fun updateEmail(newValue: String) {
        _authState.update { it.copy(email = newValue) }
    }
    fun updatePassword(newValue: String) {
        _authState.update { it.copy(password = newValue) }
    }
    fun updateEmailError(newValue: String) {
        _authState.update { it.copy(emailError = newValue) }
    }
    fun updatePasswordError(newValue: String) {
        _authState.update { it.copy(passwordError = newValue) }
    }
    fun updatePhone(newValue: String) {
        _authState.update { it.copy(phone = newValue) }
    }
    fun updatePhoneError(newValue: String) {
        _authState.update { it.copy(phoneError = newValue) }
    }
    fun updateName(newValue: String) {
        _authState.update { it.copy(name = newValue) }
    }
    fun updateNameError(newValue: String) {
        _authState.update { it.copy(nameError = newValue) }
    }
}