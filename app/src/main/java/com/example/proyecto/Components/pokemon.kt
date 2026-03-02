package com.example.proyecto.Components

import com.example.proyecto.R

data class Pokemon(
    val name: String,
    val stars: Int,
    val image: Int,
    val count: Int
)

val pokemonList = listOf(
    Pokemon("Bulbasaur", 120, R.drawable.bulbasaur, 10),
    Pokemon("Caterpie", 140, R.drawable.caterpie, 8),
    Pokemon("Psyduck", 200, R.drawable.psyduck, 21),
    Pokemon("Iggltbuff", 30, R.drawable.igglybuff, 11),
    Pokemon("Pichu", 50, R.drawable.pichu, 3),
    Pokemon("Nidoran", 180, R.drawable.nidoran, 25),
    Pokemon("Pidgey", 130, R.drawable.pidgey, 13),
    Pokemon("Rattata", 140, R.drawable.rattata, 2),
    Pokemon("Squirtle", 300, R.drawable.squirtle, 1),
)