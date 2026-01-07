package com.example.granaplay

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.granaplay.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.GameViewModel
import com.example.granaplay.data.GameViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 1. Pegar a instância do Banco e do Repositório
        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database.gameDao())

        // 2. Criar a Fábrica
        val viewModelFactory = GameViewModelFactory(repository)

        // 3. Inicializar o ViewModel (ISSO VAI DISPARAR O POPULAR BANCO AUTOMATICAMENTE)
        val viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]

        // 4. (Opcional) Teste para ver se funcionou no Logcat
        lifecycleScope.launch {
            viewModel.todosModulos.collect { lista ->
                println("TAMANHO DA LISTA DE MÓDULOS: ${lista.size}")
            }
        }
    }
}