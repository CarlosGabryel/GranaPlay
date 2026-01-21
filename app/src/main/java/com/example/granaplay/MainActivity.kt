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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Configura UI (Tela Cheia / Edge-to-Edge)
        setupEdgeToEdge()

        // 2. Inicializa dependências
        setupViewModel()

        // 3. Configura Navegação (Bottom Bar em Compose)
        setupBottomNav()
    }

    // ========================================================================
    // 1. CONFIGURAÇÃO DE UI (SYSTEM BARS)
    // ========================================================================

    private fun setupEdgeToEdge() {
        // Permite que o app desenhe atrás das barras de sistema (Status e Navigation)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Define ícones escuros na barra de status (para fundos claros)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
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
            // Estratégia de limpeza de memória crucial para integração XML/Compose
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                // Estado para rastrear a aba ativa
                var currentRouteId by remember { mutableStateOf(navController.currentDestination?.id) }

                // Listener: Atualiza a UI do menu sempre que a navegação mudar (ex: ao voltar)
                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        currentRouteId = destination.id
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                // Renderiza o Menu Customizado
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

    /**
     * Executa a navegação segura entre abas, preservando o estado e pilha.
     */
    private fun navigateToTab(navController: NavController, destinationId: Int) {
        // Evita recarregar se já estiver na tela
        if (navController.currentDestination?.id == destinationId) return

        val options = navOptions {
            // Limpa a pilha até a Home ao trocar de aba (comportamento padrão Android)
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // Evita criar múltiplas instâncias da mesma tela
            launchSingleTop = true
            // Restaura o estado anterior da aba ao voltar para ela
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

    // Suporte ao botão de voltar físico/gesto
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}