package com.example.granaplay.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Usuario::class,
        Modulo::class,
        Licao::class,
        UsuarioLicao::class,
        Questao::class,
        Alternativa::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "granaplay_database"
                )
                    .fallbackToDestructiveMigration() // Limpa o banco ao alterar versão (útil em dev)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}