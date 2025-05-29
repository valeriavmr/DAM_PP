package com.example.tp_1

import java.io.Serializable

class Mascota(var nombre: String, var edad: Int, var color: String,
              var sexo: String): Serializable {

    override fun toString(): String {
        return "Mascota(nombre='$nombre', edad=$edad, color='$color', sexo='${sexo}')"
    }
}