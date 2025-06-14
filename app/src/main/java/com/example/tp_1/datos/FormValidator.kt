package com.example.tp_1.datos

import java.util.Locale

object FormValidator {

    //valida que tenga el formato email
    fun esEmailValido(email: String): Boolean{

        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
        return regex.matches(email)

    }

    //Valida que el cel tenga un formato correcto
    fun esCelValid(telefono: String): Boolean{

        return telefono.matches(Regex("^\\d{10}$"))
    }

    //Valida que tenga 8 digitos, y al menos un numero
    fun esPassValida(password: String): Boolean {

        return password.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"))
    }

    //Función que capitaliza palabras
    fun capitalizeWords(texto: String, locale: Locale = Locale.getDefault()): String {
        return texto.lowercase(locale)
            .split(" ")
            .joinToString(" ") { palabra ->
                palabra.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                }
            }
    }

}