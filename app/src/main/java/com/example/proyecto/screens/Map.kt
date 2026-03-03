package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import com.example.proyecto.R
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens


@Composable
fun MapScreen(navController: NavController) {
    Scaffold(
        topBar = { MapTopBar() },
        bottomBar = { CustomBottomBar(navController) },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mapad),
                    contentDescription = "Mapa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.man),
                    contentDescription = "Jugador",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(70.dp)
                )

                IconButton(
                    onClick = { navController.navigate(AppScreens.CollectionScreen.name) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 120.dp, end = 40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pokeball),
                        contentDescription = "Colección",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = { navController.navigate(AppScreens.CollectionScreen.name) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 500.dp, end = 70.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pokeball),
                        contentDescription = "Colección",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = { navController.navigate(AppScreens.ShopScreen.name) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 220.dp, end = 40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tienda),
                        contentDescription = "Tienda",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { navController.navigate(AppScreens.ShopScreen.name) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 450.dp, end = 60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tienda),
                        contentDescription = "Tienda",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTopBar() {
    TopAppBar(

        title = {Text("Mapa")},
        colors = TopAppBarColors(Color.Blue, Color.Blue, Color.Blue, Color.White, Color.Blue),

        )


}

@Preview
@Composable
fun MapScreenPreview() {
    val navController = rememberNavController()
    MapScreen(navController = navController)
}