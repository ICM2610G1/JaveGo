package com.example.proyecto.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CustomBottomBar(navController: NavController) {
    BottomAppBar(
        containerColor = Color.Blue
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                navController.navigate(AppScreens.MessagesScreen.name)
            }) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Icono mensajería",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                navController.navigate(AppScreens.AccountScreen.name)
            }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Icono cuenta",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                navController.navigate(AppScreens.CollectionScreen.name)
            }) {
                Image(
                    painter = painterResource(R.drawable.pok__ball_icon),
                    contentDescription = "Icono poke bola",
                )
            }

            IconButton(onClick = {
                navController.navigate(AppScreens.MapScreen.name)
            }) {
                Image(
                    painter = painterResource(R.drawable.compass),
                    contentDescription = "Icono brújula",
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            IconButton(onClick = {
                navController.navigate(AppScreens.MissionsScreen.name)
            }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Icono calendario",
                    tint = Color.White
                )
            }
        }
    }
}
@Preview
@Composable
fun Test() {
    CustomBottomBar(rememberNavController())
}