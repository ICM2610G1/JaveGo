package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.Navigation.AppScreens
import com.example.proyecto.R
import com.example.proyecto.auth
import com.example.proyecto.database


@Composable
fun TeamScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Escoge un equipo",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 40.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedCard(
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clickable {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            database.getReference("users/$uid")
                                .updateChildren(mapOf("equipo" to "Bulbasaur"))
                        }
                        navController.navigate(AppScreens.AccountScreen.name)
                },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bulbasaur),
                        contentDescription = "Bulbasaur",
                        modifier = Modifier
                            .height(80.dp)
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Bulbasaur",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


            }


            ElevatedCard(
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clickable {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            database.getReference("users/$uid")
                                .updateChildren(mapOf("equipo" to "Charmander"))
                        }
                        navController.navigate(AppScreens.AccountScreen.name)
                    }
                    ,
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.charmander),
                            contentDescription = "Charmander",
                            modifier = Modifier
                                .height(80.dp)
                                .padding(6.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Charmander",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }


                }


            ElevatedCard(
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clickable {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            database.getReference("users/$uid")
                                .updateChildren(mapOf("equipo" to "Squirtle"))
                        }
                        navController.navigate(AppScreens.AccountScreen.name)
                }
                    ,
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.squirtle),
                        contentDescription = "Squirtle",
                        modifier = Modifier
                            .height(80.dp)
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Squirtle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


            }
        }
    }
}