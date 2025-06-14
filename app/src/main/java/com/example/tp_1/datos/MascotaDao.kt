package com.example.tp_1.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MascotaDao {

    @Insert
    fun insertMascota(mascota: Mascota)

    @Delete
    fun deleteMascota(mascota: Mascota)

    @Update
    fun updateMascota(mascota: Mascota)

    //Queries específicas
    @Query("SELECT * FROM mascotas where personaId= :personaId")
    fun getAllMascotasDePersona(personaId: Int):List<Mascota>

    @Query("SELECT * FROM mascotas where lower(nombre)= lower(:nombre) and personaId = :personaId")
    fun getMascotaByNombre(nombre:String, personaId:Int): Mascota?

    @Query("DELETE FROM mascotas where personaId =:personaId")
    fun borrarMascotasPersona(personaId:Int)
}