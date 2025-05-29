package com.example.tp_1

import android.telephony.PhoneNumberUtils
import java.io.Serializable

class Persona(var user_name: String ="", var password : String ="", var nombre : String="",
              var apellido : String ="", var email : String="", var celular : String="",
              var mascotas : MutableList<Mascota> = mutableListOf<Mascota>(), var genero: String=""):
    Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Persona) return false

        return user_name == other.user_name &&
                nombre == other.nombre &&
                apellido == other.apellido &&
                email == other.email &&
                celular == other.celular &&
                mascotas == other.mascotas &&
                genero == other.genero
    }

    override fun hashCode(): Int {
        var result = user_name.hashCode()
        result = 31 * result + nombre.hashCode()
        result = 31 * result + apellido.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + celular.hashCode()
        result = 31 * result + mascotas.hashCode()
        result = 31 * result + genero.hashCode()
        return result
    }

    override fun toString(): String {
        return "Persona(user_name='$user_name', nombre='$nombre', apellido='$apellido', email='$email', celular='$celular', mascotas=$mascotas, genero='${genero}')"
    }

}