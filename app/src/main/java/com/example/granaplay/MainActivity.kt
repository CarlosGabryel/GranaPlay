package com.example.granaplay

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.vectorResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.GameViewModel
import com.example.granaplay.data.GameViewModelFactory
import com.example.granaplay.databinding.ActivityMainBinding
import com.example.granaplay.ui.components.BottomNavItem
import com.example.granaplay.ui.components.CustomBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura Edge-to-Edge ANTES do setContentView para evitar "pulos" visuais
        setupEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inicializa dependências
        setupViewModel()

        // 3. Configura Navegação (Bottom Bar em Compose)
        setupBottomNav()
    }

    // ========================================================================
    // 1. CONFIGURAÇÃO DE UI (SYSTEM BARS)
    // ========================================================================

    private fun setupEdgeToEdge() {
        // 1. Diz para o app desenhar atrás das barras (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Define as cores como transparentes
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        // 3. Configura ícones escuros na barra de status (se o fundo for claro)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true // Tenta ícones escuros na navegação

        // --- SOLUÇÃO DO PROBLEMA ---
        // 4. Esconde a Barra de Navegação (Botões virtuais)
        // O usuário precisará deslizar de baixo para cima para vê-los.
        insetsController.hide(androidx.core.view.WindowInsetsCompat.Type.navigationBars())

        // 5. Configura para a barra aparecer temporariamente ao deslizar e sumir sozinha depois
        insetsController.systemBarsBehavior =
            androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    // ========================================================================
    // 2. VIEW MODEL E DADOS
    // ========================================================================

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database.gameDao())
        val viewModelFactory = GameViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    // ========================================================================
    // 3. NAVEGAÇÃO E MENU (COMPOSE + JETPACK NAV)
    // ========================================================================

    private fun setupBottomNav() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.composeBottomBar.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                var currentRouteId by remember { mutableStateOf(navController.currentDestination?.id) }

                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        currentRouteId = destination.id
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                CustomBottomNavigation(
                    items = getMenuItems(),
                    currentDestinationId = currentRouteId,
                    onNavigate = { destinationId ->
                        navigateToTab(navController, destinationId)
                    }
                )
            }
        }
    }

    private fun navigateToTab(navController: NavController, destinationId: Int) {
        if (navController.currentDestination?.id == destinationId) return

        val options = navOptions {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        navController.navigate(destinationId, null, options)
    }

    @Composable
    private fun getMenuItems(): List<BottomNavItem> {
        return listOf(
            BottomNavItem("Home", ImageVector.vectorResource(R.drawable.ic_home), R.id.navigation_home),
            BottomNavItem("Missões", ImageVector.vectorResource(R.drawable.ic_tasks), R.id.navigation_dashboard),
            BottomNavItem("Cidade", ImageVector.vectorResource(R.drawable.ic_city), R.id.navigation_notifications),
            BottomNavItem("Perfil", ImageVector.vectorResource(R.drawable.ic_profile), R.id.navigation_profile)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}