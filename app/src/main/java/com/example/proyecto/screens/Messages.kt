package com.example.proyecto.screens


import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.Components.CustomBottomBar
import com.google.accompanist.permissions.*
import com.example.proyecto.R


data class Contact(val id: String, val name: String, val phone: String)
data class ChatEntry(val title: String, val description: String, val gender: String = "male")


fun loadContacts(contentResolver: ContentResolver): List<Contact> {
    val contactsList = mutableListOf<Contact>()
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projection, null, null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )
    cursor?.use {
        val nameCol = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numCol = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val idCol = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
        while (it.moveToNext()) {
            val name = it.getString(nameCol) ?: "Sin nombre"
            val number = it.getString(numCol) ?: ""
            val id = it.getString(idCol)
            contactsList.add(Contact(id, name, number))
        }
    }
    return contactsList
}


@Composable
fun MessagesScreen(navController: NavController) {
    val chatList = remember { mutableStateListOf<ChatEntry>() }
    var isAddingContact by remember { mutableStateOf(false) }

    if (isAddingContact) {
        ContactsSelectionScreen(
            onContactSelected = { contact ->

                if (chatList.none { it.title == contact.name }) {
                    chatList.add(ChatEntry(contact.name, "¡Nuevo chat iniciado!", "male"))
                }
                isAddingContact = false
            },
            onBack = { isAddingContact = false }
        )
    } else {
        Scaffold(
            topBar = { MessagesTopBar() },
            bottomBar = { CustomBottomBar(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Brush.verticalGradient(listOf(Color(0xFFBBDEFB), Color.White))),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chats", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { isAddingContact = true },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Text("Añadir +", color = Color.Black)
                    }
                }

                if (chatList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay chats.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        items(chatList) { chat ->
                            MessageSubCard(chat.title, chat.description, chat.gender, navController)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsSelectionScreen(onContactSelected: (Contact) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(android.Manifest.permission.READ_CONTACTS)
    val contactsList = remember { mutableStateOf<List<Contact>>(emptyList()) }

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            contactsList.value = loadContacts(context.contentResolver)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona un contacto") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (permissionState.status.isGranted) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(contactsList.value) { contact ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onContactSelected(contact) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountCircle, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Column(Modifier.padding(start = 16.dp)) {
                                Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(contact.phone, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    }
                }
            } else {
                Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    Text("Necesitamos permiso para ver tus contactos.")
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Dar Permiso")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageSubCard(title: String, description: String, gender: String, controller: NavController) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = { controller.navigate("ChatScreen") }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val img = if (gender == "female") R.drawable.avatar1 else R.drawable.avatar2
            Image(painterResource(img), null, Modifier.size(50.dp))
            Column(Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Mensajería", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF5db3f5), titleContentColor = Color.White)
    )
}