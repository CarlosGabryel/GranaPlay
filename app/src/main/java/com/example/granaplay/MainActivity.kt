package com.example.granaplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.vectorResource // Necessário para ícones customizados (R.drawable)
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.GameViewModel
import com.example.granaplay.data.GameViewModelFactory
import com.example.granaplay.databinding.ActivityMainBinding
import com.example.granaplay.ui.components.BottomNavItem
import com.example.granaplay.ui.components.CustomBottomNavigation
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CONFIGURAÇÃO DE NAVEGAÇÃO
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // CORREÇÃO 1: Adicionei R.id.navigation_profile aqui.
        // Isso impede que a seta "voltar" apareça na tela de perfil.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // --- CONFIGURAÇÃO DO MENU COMPOSE ---
        val composeView = findViewById<ComposeView>(R.id.compose_bottom_bar)

        composeView.setContent {
            // 1. Estado para saber qual tela está ativa
            val currentRoute = remember { mutableStateOf(navController.currentDestination?.id) }

            // 2. Observar mudanças de navegação
            navController.addOnDestinationChangedListener { _, destination, _ ->
                currentRoute.value = destination.id
            }

            // 3. Definição dos Itens
            // CORREÇÃO 2: Uso correto de ImageVector.vectorResource para pegar seus desenhos da pasta drawable
            // ATENÇÃO: Os arquivos ic_home.xml, ic_tasks.xml, etc, precisam existir na pasta res/drawable!
            val menuItems = listOf(
                BottomNavItem(
                    title = "Home",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_home),
                    navDestinationId = R.id.navigation_home
                ),
                BottomNavItem(
                    title = "Tasks",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_tasks),
                    navDestinationId = R.id.navigation_dashboard
                ),
                BottomNavItem(
                    title = "City",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_city),
                    navDestinationId = R.id.navigation_notifications
                ),
                BottomNavItem(
                    title = "Profile",
                    icon = ImageVector.vectorResource(id = R.drawable.ic_profile),
                    navDestinationId = R.id.navigation_profile
                )
            )

            // 4. Renderiza o Menu Customizado
            CustomBottomNavigation(
                items = menuItems,
                currentDestinationId = currentRoute.value,
                onNavigate = { id ->
                    val options = navOptions {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                    navController.navigate(id, null, options)
                }
            )
        }

        // --- LÓGICA DE DADOS (ViewModel e Banco) ---
        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database.gameDao())
        val viewModelFactory = GameViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]

        lifecycleScope.launch {
            viewModel.todosModulos.collect { lista ->
                println("TAMANHO DA LISTA DE MÓDULOS: ${lista.size}")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}