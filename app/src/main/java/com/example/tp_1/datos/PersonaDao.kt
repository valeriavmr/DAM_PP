package com.example.tp_1.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PersonaDao {

    //Declaro las queries

    @Insert(onConflict  = OnConflictStrategy.Companion.IGNORE)
    fun InsertPersona(persona: Persona)

    @Delete
    fun DeletePersona(persona: Persona)

    @Update
    fun actualizarPersona(persona: Persona)

    //queries especificas

    @Query("Select * from personas where user_name = :userName")
    fun getPersonaPorUserName(userName: String): Persona?

    @Query("SELECT * FROM personas")
    fun getAllPersonas():List<Persona>

    @Query("SELECT * from personas where email = :email")
    fun getPersonaByEmail(email: String): Persona?

    //Devuelve a la persona y sus mascotas
    @Transaction
    @Query("SELECT * FROM personas WHERE id = :personaId")
    fun obtenerPersonaConMascotas(personaId: Int): PersonaConMascotas
}