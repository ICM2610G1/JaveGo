package com.example.proyecto.screens

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.Components.AuthViewModel
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import com.example.proyecto.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, model: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val state by model.authState.collectAsState()

    fun authenticateWithBiometrics() {
        val activity = context as? FragmentActivity
        if (activity == null) {
            Toast.makeText(context, "Error de sistema", Toast.LENGTH_SHORT).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navController.navigate(AppScreens.TeamScreen.name)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verificación requerida")
            .setSubtitle("Usa tu huella para registrarte en Jave Go")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

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
                text = "Iniciar Sesión",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 40.dp)
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

            Button(
                onClick = {
                    if (state.email.isNotEmpty() && state.password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(state.email, state.password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                authenticateWithBiometrics()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al iniciar sesión",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                    Toast.makeText(
                        context,
                        "Llena los campos para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
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
                Text("Ingresar")
            }

            TextButton(
                onClick = {
                    navController.navigate(AppScreens.RegisterScreen.name)
                }
            ) {
                Text("¿No tienes cuenta? Regístrate", color = Color(0xFF2196F3))
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