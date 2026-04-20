package com.example.proyecto.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.Components.AuthViewModel
import com.example.proyecto.Components.TwoButtonBullet
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import com.example.proyecto.auth
import com.example.proyecto.database
import com.google.firebase.auth.UserProfileChangeRequest

fun validEmailAddress(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}

fun validateForm(model: AuthViewModel, email: String, password: String): Boolean {
    if (model.authState.value.name.isEmpty()) {
        model.updateNameError("El nombre está vacío")
        return false
    } else { model.updateNameError("") }

    if (email.isEmpty()) {
        model.updateEmailError("El correo está vacío")
        return false
    } else { model.updateEmailError("") }

    if (!validEmailAddress(email)) {
        model.updateEmailError("Correo no válido")
        return false
    } else { model.updateEmailError("") }

    if (password.isEmpty()) {
        model.updatePasswordError("La contraseña está vacía")
        return false
    } else { model.updatePasswordError("") }

    if (password.length < 6) {
        model.updatePasswordError("Mínimo 6 caracteres")
        return false
    } else { model.updatePasswordError("") }

    if (model.authState.value.phone.isEmpty()) {
        model.updatePhoneError("El celular está vacío")
        return false
    } else { model.updatePhoneError("") }

    return true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, model: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val state by model.authState.collectAsState()

    Scaffold(
        topBar = { MyTopBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 50.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea una cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            TextField(
                placeholder = { Text("Nombre de usuario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 15.dp),
                onValueChange = { model.updateName(it) },
                value = state.name,
                supportingText = {
                    Text(state.nameError, color = Color.Red)
                }
            )

            TextField(
                placeholder = { Text("Correo Electrónico") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 15.dp),
                onValueChange = { model.updateEmail(it) },
                value = state.email,
                supportingText = {
                    Text(state.emailError, color = Color.Red)
                }
            )

            TextField(
                placeholder = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 15.dp),
                onValueChange = { model.updatePassword(it) },
                value = state.password,
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    Text(state.passwordError, color = Color.Red)
                }
            )

            TextField(
                placeholder = { Text("Celular") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 15.dp),
                onValueChange = { model.updatePhone(it) },
                value = state.phone,
                supportingText = {
                    Text(state.phoneError, color = Color.Red)
                }
            )

            TwoButtonBullet(Modifier.padding(32.dp, 10.dp))

            Button(
                onClick = {
                    if (validateForm(model, state.email, state.password)) {
                        auth.createUserWithEmailAndPassword(state.email, state.password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val user = auth.currentUser
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(state.name).build()
                                    user?.updateProfile(profileUpdates)

                                    val uid = auth.currentUser?.uid
                                    val myRef = database.getReference("users/$uid")


                                    myRef.setValue(mapOf(
                                        "nombre" to state.name,
                                        "correo" to state.email,
                                        "celular" to state.phone,
                                        "photoUrl" to ""
                                    ))

                                    navController.navigate(AppScreens.LoginScreen.name)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error al registrar: ${it.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 100.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            ) {
                Text("Registrarse")
            }

            Image(
                painter = painterResource(id = R.drawable.perfilhome),
                contentDescription = "Entrenadores",
                modifier = Modifier
                    .size(360.dp)
                    .padding(top = 8.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Jave Go", fontSize = 25.sp, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF5db3f5),
            titleContentColor = Color.White
        )
    )
}