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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var showOptions by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)


    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()) { success ->
        if (success) imageUri = newImageUri
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri ->
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
                    .clickable { showOptions = true }
            ) {
                Image(
                    painter = if (imageUri != null) {
                        rememberAsyncImagePainter(imageUri)
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
                text = "Nombre",
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
                onClick = { navController.navigate(AppScreens.HomeScreen.name) },
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
            onClick = { navController.navigate(route = AppScreens.HomeScreen.name) },
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