package com.example.granaplay

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupViewModel()
        setupBottomNav()
        setupObservers()
    }

    /**
     * Configura a interface para ocupar a tela toda (atrás da barra de status/navegação)
     * usando APIs modernas do AndroidX.
     */
    private fun setupEdgeToEdge() {
        // Diz à janela para não ajustar automaticamente o layout pelas barras do sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Configura as cores das barras como transparentes
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Opcional: Garante que os ícones da barra de status sejam visíveis (escuros ou claros)
        // dependendo do tema do app. Aqui forçamos ícones claros ou escuros se necessário.
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true // True = ícones escuros, False = ícones claros
    }

    /**
     * Inicializa o ViewModel e suas dependências.
     */
    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database.gameDao())
        val viewModelFactory = GameViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    /**
     * Configura a Bottom Navigation usando Jetpack Compose e ViewBinding.
     */
    private fun setupBottomNav() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.composeBottomBar.setContent {
            // Gerencia o estado da rota atual
            var currentRoute by remember { mutableStateOf(navController.currentDestination?.id) }

            // Monitora mudanças de destino de forma segura no ciclo de vida do Compose
            DisposableEffect(navController) {
                val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                    currentRoute = destination.id
                }
                navController.addOnDestinationChangedListener(listener)

                // Remove o listener quando este composable sair da tela
                onDispose {
                    navController.removeOnDestinationChangedListener(listener)
                }
            }

            // Renderiza o menu
            CustomBottomNavigation(
                items = getMenuItems(),
                currentDestinationId = currentRoute,
                onNavigate = { id -> navigateToTab(navController, id) }
            )
        }
    }

    /**
     * Lógica de navegação para as abas (evita recriar a pilha).
     */
    private fun navigateToTab(navController: NavController, destinationId: Int) {
        val options = navOptions {
            launchSingleTop = true
            restoreState = true
            // Pop até o destino inicial do gráfico para evitar empilhamento infinito
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
        navController.navigate(destinationId, null, options)
    }

    /**
     * Retorna a lista de itens do menu.
     */
    @Composable
    private fun getMenuItems(): List<BottomNavItem> {
        return listOf(
            BottomNavItem("Home", ImageVector.vectorResource(R.drawable.ic_home), R.id.navigation_home),
            BottomNavItem("Tasks", ImageVector.vectorResource(R.drawable.ic_tasks), R.id.navigation_dashboard),
            BottomNavItem("City", ImageVector.vectorResource(R.drawable.ic_city), R.id.navigation_notifications),
            BottomNavItem("Profile", ImageVector.vectorResource(R.drawable.ic_profile), R.id.navigation_profile)
        )
    }

    /**
     * Observa os dados do ViewModel.
     */
    private fun setupObservers() {
        lifecycleScope.launch {
            // Recomendado usar repeatOnLifecycle para evitar coleta em background,
            // mas mantive simples conforme o original.
            viewModel.todosModulos.collect { lista ->
                // Considere usar Logs do Android (Log.d) em vez de println
                // Log.d("MainActivity", "Módulos carregados: ${lista.size}")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}