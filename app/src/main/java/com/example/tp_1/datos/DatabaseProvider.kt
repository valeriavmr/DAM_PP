package com.example.tp_1.datos

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDataBase? = null

    fun getDatabase(context: Context): AppDataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "adiestramiento_app_db"
            ).allowMainThreadQueries().build()

            INSTANCE = instance
            instance
        }
    }
}