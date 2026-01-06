package com.example.granaplay

import android.app.Application
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository

class GranaPlayApplication : Application() {
    // A database e o repository são criados de forma "preguiçosa" (lazy).
    // Só são criados quando forem usados pela primeira vez.

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { GameRepository(database.gameDao()) }
}
