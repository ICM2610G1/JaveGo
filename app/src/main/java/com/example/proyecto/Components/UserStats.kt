package com.example.proyecto.Components

import com.example.proyecto.database


fun starsNeededForLevel(level: Int): Int = level * 100


fun addStarsToUser(
    uid: String,
    starsToAdd: Int,
    onResult: (newStars: Int, newLevel: Int, leveledUp: Boolean) -> Unit = { _, _, _ -> }
) {
    val ref = database.getReference("users/$uid")
    ref.get().addOnSuccessListener { snapshot ->
        val currentStars = (snapshot.child("estrellas").value as? Long)?.toInt() ?: 0
        val currentLevel = (snapshot.child("nivel").value as? Long)?.toInt() ?: 1

        var newStars = currentStars + starsToAdd
        var newLevel = currentLevel
        var leveledUp = false


        while (newStars >= starsNeededForLevel(newLevel)) {
            newStars -= starsNeededForLevel(newLevel)
            newLevel++
            leveledUp = true
        }

        ref.updateChildren(mapOf("estrellas" to newStars, "nivel" to newLevel))
            .addOnSuccessListener { onResult(newStars, newLevel, leveledUp) }
    }
}

object StarRewards {
    const val POKEMON_CAPTURADO = 50       // cada captura
    const val INFILTRACION_EXITOSA = 120   // ganar el juego del mapa
    const val RACHA_DIARIA = 30            // login diario
    const val MISION_COMPLETADA = 80       // completar misión
}
