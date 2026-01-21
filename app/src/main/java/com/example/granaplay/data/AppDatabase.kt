package com.example.granaplay.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Banco de dados principal do Room.
 * Gerencia a conexão e a criação das instâncias dos DAOs.
 */
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

        /**
         * Retorna a instância única do banco de dados (Singleton).
         * Utiliza Double-Checked Locking para garantir thread-safety.
         *
         * @warning [fallbackToDestructiveMigration] está ativo.
         * Isso apagará todos os dados se a versão do banco mudar.
         * Para produção, implemente Migrations.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "granaplay_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}