package com.example.proyecto.screens

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R

@Composable

fun MissionsScreen(navController: NavController){

    Scaffold(
        topBar = { MissionsTopBar() },
        bottomBar = {
            CustomBottomBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ElevatedCardForMissions()

        }  }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsTopBar() {
    TopAppBar(

        title = {Text("Misiones")},
        colors = TopAppBarColors(Color.Blue, Color.Blue, Color.Blue, Color.White, Color.Blue),

        )


}

@Composable
fun ElevatedCardForMissions() {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Misiones pendientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            MissionSubCard(
                title = "Atrapa 5 Pokémon nuevos",
                description = "+500"
            )

            Spacer(modifier = Modifier.height(8.dp))


            MissionSubCard(

                title = "Agrega 3 amigos nuevos",

                description = "+200"
            )

            Spacer(modifier = Modifier.height(8.dp))


            MissionSubCard(
                title = "Completa 3 misiones en un día",
                description = "+300"
            )

            Spacer(modifier = Modifier.height(8.dp))


            MissionSubCard(

                title = "Compra tu primer Pokemon",
                description = "+400"
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }




    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Racha Diaria",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Inicia sesión cada día para obtener mas recompensas.",
                fontSize = 14.sp,
                color = Color.Gray

            )

            ElevatedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            ) {
                Text("Recibir recompensa")
            }
        }
    }
}


@Composable
fun MissionSubCard(
    title: String,
    description: String
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.pok__ball_icon),
                contentDescription = "Icono poke bola",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )


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