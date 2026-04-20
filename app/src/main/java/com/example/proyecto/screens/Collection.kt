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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Components.Pokemon
import com.example.proyecto.R
import com.example.proyecto.auth
import com.example.proyecto.database


data class PokemonCapturado(
    val nombre: String = "",
    val id: Int = 0
)

@Composable
fun CollectionScreen(navController: NavController) {
    val uid = auth.currentUser?.uid
    var pokemonesCapturados by remember { mutableStateOf<List<PokemonCapturado>>(emptyList()) }


    LaunchedEffect(uid) {
        if (uid != null) {
            database.getReference("users/$uid/pokemons")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lista = mutableListOf<PokemonCapturado>()
                        task.result.children.forEach { snap ->
                            val nombre = snap.child("nombre").value as? String ?: ""
                            val id = (snap.child("id").value as? Long)?.toInt() ?: 0
                            lista.add(PokemonCapturado(nombre, id))
                        }
                        pokemonesCapturados = lista
                    }
                }
        }
    }

    Scaffold(
        topBar = { CollectionTopBar() },
        bottomBar = { CustomBottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF79BAEC), Color.White)
                    )
                ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            PokemonSearchBarExample()

            ElevatedButton(
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                )
            ) {
                Text("Vender", modifier = Modifier.padding(horizontal = 32.dp))
            }

            if (pokemonesCapturados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No has capturado ningún Pokémon aún", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
                ) {
                    items(pokemonesCapturados) { pokemon ->
                        PokemonCapturedCard(pokemon)
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonCapturedCard(pokemon: PokemonCapturado) {
    val imageRes = when (pokemon.nombre) {
        "Pichu" -> R.drawable.pichu
        "Squirtle" -> R.drawable.squirtle
        "Psyduck" -> R.drawable.psyduck
        "Rattata" -> R.drawable.rattata
        "Pidgey" -> R.drawable.pidgey
        "Nidoran" -> R.drawable.nidoran
        "Igglybuff" -> R.drawable.igglybuff
        else -> R.drawable.pichu
    }

    ElevatedCard(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = pokemon.nombre,
                modifier = Modifier.height(80.dp)
            )
            Text(pokemon.nombre, fontWeight = FontWeight.SemiBold)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTopBar() {
    CenterAlignedTopAppBar(

        title = {Text("Colección", fontWeight = FontWeight.Bold, fontSize = 24.sp)},
        colors = TopAppBarColors(Color(0xFF5db3f5), Color(0xFF5db3f5), Color(0xFF5db3f5), Color.White, Color(0xFF5db3f5)),

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonSearchBarExample() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {  },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Buscar Pokemon...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.DarkGray
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
    }
}

@Composable
fun PokemonCard(pokemon: Pokemon){
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

            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).background((Color.Blue), shape = CircleShape)
            ){
                Text(
                    text = pokemon.count.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

        }
    }
}
@Preview
@Composable
fun CollectionPreview(){
    val navController = rememberNavController()
    CollectionScreen(navController)
}

