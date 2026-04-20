package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import com.example.proyecto.auth
import com.example.proyecto.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val uid = auth.currentUser?.uid
    var nombre by remember { mutableStateOf(auth.currentUser?.displayName ?: "Usuario") }
    var correo by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }


    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid != null) {
            database.getReference("users/$uid")
                .get()
                .addOnSuccessListener { snapshot ->
                    nombre = snapshot.child("nombre").value as? String ?: nombre
                    correo = snapshot.child("correo").value as? String ?: ""
                    celular = snapshot.child("celular").value as? String ?: ""
                    photoUrl = snapshot.child("photoUrl").value as? String ?: ""
                }
        }
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if (uid != null) {

                            database.getReference("users/$uid")
                                .removeValue()
                                .addOnSuccessListener {

                                    auth.currentUser?.delete()
                                        ?.addOnSuccessListener {
                                            navController.navigate(AppScreens.LoginScreen.name) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                        ?.addOnFailureListener {

                                            navController.navigate(AppScreens.LoginScreen.name) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                }
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = { AccountTopBar() },
        bottomBar = { CustomBottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF79BAEC),
                            Color.White
                        )
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(230.dp)
                    .padding(top = 8.dp, bottom = 20.dp)
            ) {
                Image(
                    painter = if (photoUrl.isNotEmpty()) {
                        rememberAsyncImagePainter(photoUrl)
                    } else {
                        painterResource(id = R.drawable.avatar1)
                    },
                    contentDescription = "Avatar de usuario",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = nombre,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
            )

            Text(
                text = "Nivel: 12 - Estrellas: 320",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 30.dp)
            )

            AccountButtons(Modifier.padding(32.dp, 10.dp), navController)


            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEEEEEE),
                    contentColor = Color.Red
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar cuenta")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Mi cuenta", fontWeight = FontWeight.Bold, fontSize = 25.sp) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF5db3f5),
            titleContentColor = Color.White
        )
    )
}

@Composable
fun AccountButtons(modifier: Modifier, navController: NavController) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            content = {
                Text(
                    text = "Editar Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Blue
                )
            },
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppScreens.EditProfileScreen.name) },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        )

        VerticalDivider(
            modifier = Modifier.height(35.dp),
            color = Color.Gray
        )

        Button(
            modifier = Modifier.weight(1f),
            content = {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Red
                )
            },
            onClick = {
                auth.signOut()
                navController.navigate(AppScreens.LoginScreen.name) {
                    popUpTo(0) { inclusive = true }
                }
            },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountPreview() {
    val navController = rememberNavController()
    AccountScreen(navController)
}