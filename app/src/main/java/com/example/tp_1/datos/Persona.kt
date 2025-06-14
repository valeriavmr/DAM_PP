package com.example.tp_1.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var user_name: String = "",
    var password: String = "",
    var nombre: String = "",
    var apellido: String = "",
    var email: String = "",
    var celular: String = "",
    var genero: String = ""
): Serializable