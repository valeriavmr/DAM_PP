package com.example.tp_1.datos

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Persona::class, Mascota::class],
    version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase(){
    abstract fun personaDao(): PersonaDao
    abstract fun mascotaDao(): MascotaDao
}