package com.example.proyecto.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Components.StarRewards
import com.example.proyecto.Components.addStarsToUser
import com.example.proyecto.Components.starsNeededForLevel
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import com.example.proyecto.auth
import com.example.proyecto.database

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val uid = auth.currentUser?.uid
    var nombre by remember { mutableStateOf(auth.currentUser?.displayName ?: "Usuario") }
    var photoUrl by remember { mutableStateOf("") }
    var estrellas by remember { mutableStateOf(0) }
    var nivel by remember { mutableStateOf(1) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    var showLevelUp by remember { mutableStateOf(false) }
    var newLevelReached by remember { mutableStateOf(1) }


    DisposableEffect(uid) {
        if (uid == null) return@DisposableEffect onDispose {}
        val ref = database.getReference("users/$uid")
        val listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                nombre   = snapshot.child("nombre").value as? String ?: auth.currentUser?.displayName ?: "Usuario"
                photoUrl = snapshot.child("photoUrl").value as? String ?: ""
                estrellas = (snapshot.child("estrellas").value as? Long)?.toInt() ?: 0
                nivel    = (snapshot.child("nivel").value as? Long)?.toInt() ?: 1
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        onDispose { ref.removeEventListener(listener) }
    }


    val starsNeeded = starsNeededForLevel(nivel)
    val progress = (estrellas.toFloat() / starsNeeded).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "xpBar"
    )


    if (showLevelUp) {
        AlertDialog(
            onDismissRequest = { showLevelUp = false },
            title = {
                Text(
                    "🎉 ¡NIVEL $newLevelReached!",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Color(0xFF1565C0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("¡Has subido al nivel $newLevelReached!", textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⭐".repeat(5), fontSize = 28.sp, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = { showLevelUp = false }) {
                    Text("¡Genial!", fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                }
            }
        )
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    if (uid != null) {
                        database.getReference("users/$uid").removeValue().addOnSuccessListener {
                            auth.currentUser?.delete()?.addOnCompleteListener {
                                navController.navigate(AppScreens.LoginScreen.name) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
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
                .background(Brush.verticalGradient(listOf(Color(0xFF79BAEC), Color.White)))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))


            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = if (photoUrl.isNotEmpty()) rememberAsyncImagePainter(photoUrl)
                    else painterResource(id = R.drawable.avatar1),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(nombre, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(20.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF2196F3), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$nivel",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Nivel $nivel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1565C0)
                                )
                                Text(
                                    text = levelTitle(nivel),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$estrellas",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA000)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progreso al nivel ${nivel + 1}", fontSize = 12.sp, color = Color.Gray)
                        Text("$estrellas / $starsNeeded ⭐", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(6.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E0E0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Faltan ${starsNeeded - estrellas} ⭐ para subir de nivel",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    if (uid != null) {
                        addStarsToUser(uid, 50) { _, newLevel, leveledUp ->
                            if (leveledUp) {
                                newLevelReached = newLevel
                                showLevelUp = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA000),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ 50 ⭐ (Demo)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))


            AccountButtons(Modifier.padding(horizontal = 20.dp), navController)

            Spacer(modifier = Modifier.height(12.dp))


            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEEEEEE),
                    contentColor = Color.Red
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar cuenta")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


fun levelTitle(level: Int): String = when {
    level <= 2  -> "Entrenador Novato"
    level <= 5  -> "Entrenador Intermedio"
    level <= 9  -> "Entrenador Experto"
    level <= 14 -> "Maestro Pokémon"
    else        -> "Leyenda Pokémon ⚡"
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            content = {
                Text("Editar Perfil", fontSize = 18.sp, fontWeight = FontWeight.Normal, color = Color.Blue)
            },
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate(AppScreens.EditProfileScreen.name) },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        )
        VerticalDivider(modifier = Modifier.height(35.dp), color = Color.Gray)
        Button(
            modifier = Modifier.weight(1f),
            content = {
                Text("Cerrar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Normal, color = Color.Red)
            },
            onClick = {
                auth.signOut()
                navController.navigate(AppScreens.LoginScreen.name) {
                    popUpTo(0) { inclusive = true }
                }
            },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountPreview() {
    AccountScreen(rememberNavController())
}