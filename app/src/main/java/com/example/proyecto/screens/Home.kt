package com.example.proyecto.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto.R
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Components.TwoButtonBullet
import com.example.proyecto.Navigation.AppScreens

@Composable

fun HomeScreen(navController: NavController){

    Scaffold(
        topBar = { MyTopBar() },

        ){padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)){

        Column(modifier = Modifier.fillMaxSize().padding(top = 50.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Crea una cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            TextField(
                placeholder = {
                    Text("Correo Electrónico")
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 15.dp),
                onValueChange = {},
                value = ""
            )

            TextField(
                placeholder = {
                    Text("Celular")
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 15.dp),
                onValueChange = {},
                value = ""
            )

            TwoButtonBullet(
                Modifier.padding(32.dp, 10.dp)
            )

            Button(
                onClick = { navController.navigate(AppScreens.TeamScreen.name) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 100.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            ) {
                Text("Registrarse")
            }

            }

            Image(
                painter = painterResource(id = R.drawable.perfilhome),
                contentDescription = "Entrenadores",
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Fit
            )

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MyTopBar(){
    CenterAlignedTopAppBar(
        title = {Text("Jave Go", fontSize = 25.sp, fontWeight = FontWeight.Bold)},
        colors = TopAppBarColors(Color(0xFF5db3f5), Color(0xFF5db3f5), Color(0xFF5db3f5), Color.White, Color(0xFF5db3f5))
    )
}

@Preview
@Composable
fun HomePreview(){
    val navController = rememberNavController()
    HomeScreen(navController)
}

