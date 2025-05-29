package com.example.tp_1

object FormValidator {

    //valida que tenga el formato email
    fun esEmailValido(email: String): Boolean{

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }

    fun esCelValid(telefono: String): Boolean{

        return telefono.matches(Regex("^\\d{10}$"))
    }

    //Valida que tenga 8 digitos, y al menos un numero
    fun esPassValida(password: String): Boolean {

        return password.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"))
    }

}