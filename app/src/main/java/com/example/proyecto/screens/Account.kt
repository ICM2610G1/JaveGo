package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R


@Composable
fun AccountScreen(navController: NavController) {

    Scaffold(
        topBar = { AccountTopBar() },
        bottomBar = {
            CustomBottomBar(navController)
        }
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
            Text(
                text = "Nombre",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            HorizontalDivider(thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp))

            Text(
                text = "Nivel: 12 - Estrellas: 320",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                modifier = Modifier.padding(
                    bottom = 30.dp
                )
            )

            AccountButtons(Modifier.padding(32.dp, 10.dp), navController)

            Button(
                onClick = {navController.navigate(AppScreens.HomeScreen.name)},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEEEEEE),
                    contentColor = Color.Red
                )){

                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )

                Text("Eliminar cuenta")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopBar() {
    CenterAlignedTopAppBar(
            title = {Text("Mi cuenta", fontWeight = FontWeight.Bold, fontSize = 25.sp)},
            colors = TopAppBarColors(Color(0xFF5db3f5), Color(0xFF5db3f5), Color(0xFF5db3f5), Color.White, Color(0xFF5db3f5)),
    )
}

@Composable
fun AccountButtons(modifier: Modifier, navController : NavController) {
    Row(
        modifier = modifier.clip(
            RoundedCornerShape(16.dp)
        ).background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button (
            content ={
                Text(
                    text = "Editar Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Blue
                )
            },
            modifier = Modifier.weight(1f),
            onClick = {},
            shape = RectangleShape,
            colors = ButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.Gray
            )
        )
        VerticalDivider(
            modifier = Modifier.height(35.dp),
            color = Color.Gray
        )
        Button (
            modifier = Modifier.weight(1f),
            content = {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Red
                )
            },
            onClick = {navController.navigate(route = AppScreens.HomeScreen.name)},
            shape = RectangleShape,
            colors = ButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.Gray
            )
        )
    }
}

@Preview
@Composable
fun AccountPreview(){
    val navController = rememberNavController()
    AccountScreen(navController)
}