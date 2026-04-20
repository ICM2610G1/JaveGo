package com.example.proyecto.screens

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.example.proyecto.auth
import com.example.proyecto.database
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    val uid = auth.currentUser?.uid
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var equipo by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var savedPhotoUrl by remember { mutableStateOf("") }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var showOptions by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }


    var showReauthDialog by remember { mutableStateOf(false) }
    var passwordReauth by remember { mutableStateOf("") }
    var reauthError by remember { mutableStateOf("") }

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
                        equipo = snapshot.child("equipo").value as? String ?: ""
                        val saved = snapshot.child("photoUrl").value as? String ?: ""
                        if (saved.isNotEmpty()) savedPhotoUrl = saved
                    }
                }
        }
    }

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

    val equipoInfo = when (equipo) {
        "Bulbasaur" -> Pair(R.drawable.bulbasaur, "Bulbasaur")
        "Charmander" -> Pair(R.drawable.charmander, "Charmander")
        "Squirtle" -> Pair(R.drawable.squirtle, "Squirtle")
        else -> Pair(R.drawable.charmander, equipo.ifEmpty { "Sin equipo" })
    }


    fun guardarEnDB(photoUrl: String) {
        if (uid == null) return
        database.getReference("users/$uid")
            .updateChildren(
                mapOf(
                    "nombre" to nombre,
                    "correo" to correo.trim(),
                    "celular" to celular,
                    "photoUrl" to photoUrl
                )
            )
            .addOnSuccessListener {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nombre).build()
                auth.currentUser?.updateProfile(profileUpdates)
                isSaving = false
                navController.popBackStack()
            }
            .addOnFailureListener {
                isSaving = false
                Toast.makeText(context, "Error al guardar datos", Toast.LENGTH_SHORT).show()
            }
    }


    fun subirFotoYGuardar() {
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance()
                .reference.child("avatars/$uid.jpg")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        guardarEnDB(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    guardarEnDB(savedPhotoUrl)
                }
        } else {
            guardarEnDB(savedPhotoUrl)
        }
    }


    fun actualizarCorreoYGuardar() {
        auth.currentUser?.updateEmail(correo.trim())
            ?.addOnSuccessListener {
                subirFotoYGuardar()
            }
            ?.addOnFailureListener { e ->
                isSaving = false
                Toast.makeText(context, "Error al cambiar correo: ${e.message}", Toast.LENGTH_LONG).show()
            }
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
                    if (cameraPermissionState.status.isGranted) openCamera()
                    else cameraPermissionState.launchPermissionRequest()
                    showOptions = false
                }) { Text("Cámara") }
            }
        )
    }


    if (showReauthDialog) {
        AlertDialog(
            onDismissRequest = {
                showReauthDialog = false
                passwordReauth = ""
                reauthError = ""
            },
            title = { Text("Confirma tu identidad") },
            text = {
                Column {
                    Text(
                        "Para cambiar el correo ingresa tu contraseña actual:",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = passwordReauth,
                        onValueChange = {
                            passwordReauth = it
                            reauthError = ""
                        },
                        placeholder = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (reauthError.isNotEmpty()) {
                        Text(
                            reauthError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val user = auth.currentUser
                    val emailActual = user?.email ?: ""
                    val credential = EmailAuthProvider.getCredential(emailActual, passwordReauth)

                    user?.reauthenticate(credential)
                        ?.addOnSuccessListener {
                            showReauthDialog = false
                            passwordReauth = ""
                            reauthError = ""
                            isSaving = true
                            actualizarCorreoYGuardar()
                        }
                        ?.addOnFailureListener {
                            reauthError = "Contraseña incorrecta, intenta de nuevo"
                        }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showReauthDialog = false
                    passwordReauth = ""
                    reauthError = ""
                }) {
                    Text("Cancelar")
                }
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
                    painter = when {
                        imageUri != null -> rememberAsyncImagePainter(imageUri)
                        savedPhotoUrl.isNotEmpty() -> rememberAsyncImagePainter(savedPhotoUrl)
                        else -> painterResource(id = R.drawable.avatar1)
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(0xFF2196F3), shape = CircleShape)
                        .padding(6.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Toca la foto para cambiarla",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                modifier = Modifier.padding(top = 10.dp)
            )

            Text(
                "Nivel 12 • ⭐ 340",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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
                            Text(equipoInfo.second, color = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = equipoInfo.first),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                enabled = !isSaving,
                onClick = {
                    if (uid == null) return@Button
                    val correoOriginal = auth.currentUser?.email ?: ""
                    val correoNuevo = correo.trim()

                    if (correoNuevo != correoOriginal && correoNuevo.isNotEmpty()) {

                        showReauthDialog = true
                    } else {

                        isSaving = true
                        subirFotoYGuardar()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar cambios", fontWeight = FontWeight.Bold)
                }
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
            textStyle = TextStyle(
                textAlign = TextAlign.End,
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier.weight(2f)
        )
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}