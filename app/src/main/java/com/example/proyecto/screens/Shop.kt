package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Components.Pokemon
import com.example.proyecto.Components.pokemonList

@Composable

fun ShopScreen(navController: NavController){

    Scaffold(
        topBar = { ShopTopBar() },
        bottomBar = {
            CustomBottomBar(navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF79BAEC),
                            Color.White
                        )
                    )
                ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {



                Text("⭐250", fontSize = 40.sp, color = Color.Black, modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp), fontWeight = FontWeight.Bold)



            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 40.dp)
            ) {
                items(pokemonList){
                        pokemon -> PokemonCardShop(pokemon)
                }
            }


        }
        Text("Solo aqui compra tus Pokémon Shiny", fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Color.LightGray, modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopTopBar() {
    CenterAlignedTopAppBar(
        title = {Text("Tienda", fontWeight = FontWeight.Bold, fontSize = 25.sp)},
        colors = TopAppBarColors(Color(0xFF5db3f5), Color(0xFF5db3f5), Color(0xFF5db3f5), Color.White, Color(0xFF5db3f5)),
    )
}

@Composable
fun PokemonCardShop(pokemon: Pokemon){
    ElevatedCard(
        modifier = Modifier.padding(8.dp). fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ){
        Box{
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Image(
                    painter = painterResource(id = pokemon.image),
                    contentDescription = pokemon.name,
                    modifier = Modifier.height(80.dp)
                )

                Text(pokemon.name, fontWeight = FontWeight.SemiBold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ ${pokemon.stars}")
                }
            }

        }
    }
}