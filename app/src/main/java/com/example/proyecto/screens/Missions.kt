package com.example.proyecto.screens

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R

@Composable

fun MissionsScreen(navController: NavController){


    val scrollState = rememberScrollState()
    Scaffold(
        topBar = { MissionsTopBar() },
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
                )

                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoSection()
            Spacer(modifier = Modifier.height(2.dp))
            ElevatedCardForMissions()

        }  }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsTopBar() {
    CenterAlignedTopAppBar(
        title = {Text("Misiones", fontWeight = FontWeight.Bold, fontSize = 24.sp)},
        colors = TopAppBarColors(Color(0xFF5db3f5), Color(0xFF5db3f5), Color(0xFF5db3f5), Color.White, Color(0xFF5db3f5)),
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Racha Diaria",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia sesión cada día para obtener mas recompensas.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()

            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val blue = Color(0xFF90CAF9)
                val red = Color.Red
                val gray = Color(0xFFE0E0E0)
                FireIcon(color = blue)
                FireIcon(color = blue)
                FireIcon(color = blue)
                FireIcon(color = red)
                FireIcon(color = gray)
                FireIcon(color = gray)
                FireIcon(color = gray)

            }

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

@Composable
fun FireIcon(color: Color){
    Icon(
        imageVector = Icons.Default.Whatshot,
        contentDescription = "Fire Icon",
        tint = color,
        modifier = Modifier.size(45.dp)
    )
}

@Composable
fun InfoSection(){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
        ) {

        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = Color.White,

            border = BorderStroke(4.dp, Color.White),
            shadowElevation = 4.dp
        ) {
            Image(

                painter = painterResource(R.drawable.profile2),
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize()

            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Nivel 12",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF455A64)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(12.dp)

                    .clip(RoundedCornerShape(6.dp))

                    .background(Color(0xFF9EA8C7).copy(alpha = 0.5f))
            ){

                Box(
                    modifier = Modifier.fillMaxWidth(0.65f)
                        .fillMaxHeight()
                        .background(Color(0xFF5C88C2))
                )
            }

            Text(
                text = "1640/2000",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun MissionsPreview(){
    val navController = rememberNavController()
    MissionsScreen(navController)
}