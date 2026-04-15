package com.example.proyecto.screens

import android.provider.CalendarContract
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import org.intellij.lang.annotations.JdkConstants

@Composable

fun MessagesScreen(navController: NavController){
    Scaffold(
        topBar = { MessagesTopBar() },
        bottomBar = {
            CustomBottomBar(navController)
        },
        containerColor = Color.Transparent
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFBBDEFB),
                            Color.White
                        )
                    )
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chats",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(Color.White)) {
                    Text(
                       text =  "Edit",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            ElevatedCardForMessages(navController)


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar() {
    CenterAlignedTopAppBar(
        title = {Text("Mensajeria", fontWeight = FontWeight.Bold, fontSize = 24.sp)},
        colors = TopAppBarColors(Color(0xFF5db3f5), Color(0xFF5db3f5), Color(0xFF5db3f5), Color.White, Color(0xFF5db3f5)),

        )
}

@Composable
fun ElevatedCardForMessages(navController: NavController) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)

    ) {LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        item {
            MessageSubCard(
                title = "Laura ❤",
                description = "Necesito 3 estrellas para subir de nivel :(",
                gender = "female",
                controller = navController
            )
        }

        item {
            MessageSubCard(
                title = "Samuel",
                description = "Ayer no entre, perdi la racha jsjs 😆",
                gender = "male",
                controller = navController
            )
        }

        item {
            MessageSubCard(
                title = "Sara 🫶",
                description = "Subi al nivel 15",
                gender = "female",
                controller = navController
            )
        }

        item {
            MessageSubCard(
                title = "Angel 🙈",
                description = "Voy a tratar de conseguir un shiny hoy!!!",
                gender = "male",
                controller = navController
            )
        }

        item {
            MessageSubCard(
                title = "Juliana",
                description = "¿Ya viste la mision nueva? 😊",
                gender = "female",
                controller = navController
            )
        }

        item {
            MessageSubCard(
                title = "Andres",
                description = "Hoy si me fue re bien con las misiones",
                gender = "male",
                controller = navController
            )
        }
    }
        Spacer(modifier = Modifier.height(10.dp))

    }

    Spacer(modifier = Modifier.height(12.dp))

    /*Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f).background(Color.Red)) {
            MessageSearchBarExample()
        }
        Box(
            modifier = Modifier.padding(6.dp).background((Color.Blue), shape = CircleShape)
        ){
            Text(
                text = "+",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }*/
}

@Composable
fun MessageSubCard(title: String, description: String, gender:String  = "female",  controller: NavController
    ) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            controller.navigate(AppScreens.ChatScreen.name)
        },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (gender == "female"){
              Image(
                painter = painterResource(R.drawable.avatar1),
                contentDescription = "Icono poke bola",
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 12.dp)
                )
            }
            if (gender == "male"){
                Image(
                    painter = painterResource(R.drawable.avatar2),
                    contentDescription = "Icono poke bola",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 12.dp)
                )
            }


            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }

}

@Preview
@Composable
fun MessagesPreview(){
    val navController = rememberNavController()
    MessagesScreen(navController)
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageSearchBarExample() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {  },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Buscar por nombre") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.DarkGray
            )
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
    }
}*/