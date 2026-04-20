package com.example.proyecto.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.util.UUID
import androidx.compose.runtime.LaunchedEffect
import com.example.proyecto.auth
import com.example.proyecto.database
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    val uid = auth.currentUser?.uid
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        if (uid != null) {
            database.getReference("users/$uid")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        nombre = snapshot.child("nombre").value as? String ?: ""
                        correo = snapshot.child("correo").value as? String ?: ""
                        celular = snapshot.child("celular").value as? String ?: ""
                    }
                }
        }
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var showOptions by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) imageUri = newImageUri
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    val openCamera = {
        val file = File(context.filesDir, "avatar_${UUID.randomUUID()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        newImageUri = uri
        cameraLauncher.launch(uri)
    }

    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Actualizar foto de perfil") },
            text = { Text("¿Cómo deseas subir tu nueva foto?") },
            confirmButton = {
                TextButton(onClick = {
                    galleryLauncher.launch("image/*")
                    showOptions = false
                }) { Text("Galería") }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (cameraPermissionState.status.isGranted) {
                        openCamera()
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                    showOptions = false
                }) { Text("Cámara") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF5db3f5))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF79BAEC), Color.White))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { showOptions = true },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = if (imageUri != null) rememberAsyncImagePainter(imageUri) else painterResource(id = R.drawable.avatar1),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(5.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                modifier = Modifier.padding(top = 10.dp)
            )

            Text("Nivel 12 • ⭐ 340", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditableInfoRow("Correo", correo) { correo = it }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    EditableInfoRow("Celular", celular) { celular = it }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Equipo", color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rojo", color = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(painter = painterResource(id = R.drawable.charmander), contentDescription = null, modifier = Modifier.size(30.dp))
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (uid != null) {
                        database.getReference("users/$uid")
                            .updateChildren(mapOf(
                                "nombre" to nombre,
                                "correo" to correo,
                                "celular" to celular
                            ))
                            .addOnSuccessListener {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre).build()
                                auth.currentUser?.updateProfile(profileUpdates)
                                navController.popBackStack()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { /* Acción eliminar */ },
                modifier = Modifier.fillMaxWidth().padding(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red),
                shape = RoundedCornerShape(15.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar cuenta", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditableInfoRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, modifier = Modifier.weight(1f))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(textAlign = TextAlign.End, fontSize = 16.sp, color = Color.Black),
            modifier = Modifier.weight(2f)
        )
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}