package com.example.tp_1.datos

import androidx.room.Embedded
import androidx.room.Relation

data class PersonaConMascotas(
    @Embedded val persona: Persona,

    @Relation(
        parentColumn = "id",
        entityColumn = "personaId"
    )
    val mascotas: List<Mascota>
)