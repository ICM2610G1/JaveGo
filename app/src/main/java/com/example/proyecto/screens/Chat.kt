package com.example.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.Components.messages
x
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {
      var inputText by remember { mutableStateOf("") }

      Scaffold(
      topBar = {
            CenterAlignedTopAppBar(
                  title = { Text("Nombre", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                  },
                  colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF5db3f5),
                        titleContentColor = Color.White
                  )
            )
      },
      bottomBar = {
            Row(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
            ) {
                  TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Escribir un mensaje") },
                        modifier = Modifier.weight(1f).padding(bottom = 10.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                              focusedIndicatorColor = Color.Transparent,
                              unfocusedIndicatorColor = Color.Transparent
                        )
                  )
                  Button(
                        onClick = {},
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                  ) {
                        Text(">", color = Color.White, fontSize = 18.sp)
                  }
            }
            }
      ) { padding ->

      LazyColumn(
            modifier = Modifier
                  .fillMaxSize()
                  .padding(padding)
                  .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
      ) {
            items(messages) { message ->
                  Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.me) Arrangement.End else Arrangement.Start
                  ) {
                  Box(
                        modifier = Modifier.widthIn(max = 260.dp).background(
                        color = if (message.me) Color(0xFF2196F3) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(
                              topStart = 16.dp,
                              topEnd = 16.dp,
                              bottomStart = if (message.me) 16.dp else 4.dp,
                              bottomEnd = if (message.me) 4.dp else 16.dp
                        )
                  ).padding(horizontal = 14.dp, vertical = 10.dp)
                  ) {
                  Text(
                        text = message.text,
                        color = if (message.me) Color.White else Color.Black,
                        fontSize = 15.sp
                  )
                  }
            }
            }
      }
      }
}