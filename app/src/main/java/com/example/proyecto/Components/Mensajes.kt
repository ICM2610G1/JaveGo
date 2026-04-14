package com.example.proyecto.Components

import androidx.compose.runtime.remember

//temporal mientras implementamos el chat de forma correcta, creria que se puede eliminar luego
data class Message(val text: String, val me: Boolean)

val messages = listOf(
        Message("Hola! Cómo estás?", false),
        Message("Todo bien, y tú?", true),
        Message("Bien también! Ya atrapaste algo nuevo?", false),
        Message("Sí! Acabo de atrapar uno muy raro", true),
        Message("En serio?? Qué envidia jaja", false),
        Message("Te lo voy a compartir cuando pueda", true),
        Message("Woow qué lindo! Dónde lo atrapaste?", false),
        Message("Aquí cerca, yendo hacia el parque", true),
    )