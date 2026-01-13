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

        setupEdgeToEdge()     // 1. Configura tela cheia
        setupViewModel()      // 2. Inicializa dados
        setupBottomNav()      // 3. Configura navegação e menu inferior
    }

    // ========================================================================
    // CONFIGURAÇÃO DE UI E SISTEMA
    // ========================================================================

    private fun setupEdgeToEdge() {
        // Permite que o app desenhe atrás das barras de sistema (Status e Navigation)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Define se os ícones da barra de status devem ser escuros (true) ou claros (false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = GameRepository(database.gameDao())
        val viewModelFactory = GameViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    // ========================================================================
    // NAVEGAÇÃO E MENU INFERIOR (COMPOSE)
    // ========================================================================

    private fun setupBottomNav() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.composeBottomBar.apply {
            // CRUCIAL: Garante que o Compose limpe a memória corretamente
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                // Estado local para saber qual aba está ativa e pintar o ícone corretamente
                var currentRouteId by remember { mutableStateOf(navController.currentDestination?.id) }

                // Listener para atualizar a aba selecionada quando a navegação ocorrer (inclusive via Back Button)
                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        currentRouteId = destination.id
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                // Renderiza o componente visual do menu
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
        // Evita recarregar a tela se o usuário clicar na aba que já está aberta
        if (navController.currentDestination?.id == destinationId) return

        val options = navOptions {
            // Salva o estado da tela ao sair e restaura ao voltar
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

    // Suporte para o botão "Voltar" físico do Android
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}