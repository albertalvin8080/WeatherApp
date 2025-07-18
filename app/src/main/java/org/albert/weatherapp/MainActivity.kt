package org.albert.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.albert.weatherapp.api.WeatherService
import org.albert.weatherapp.db.fb.FBDatabase
import org.albert.weatherapp.ui.dialog.CityDialog
import org.albert.weatherapp.ui.nav.BottomNavBar
import org.albert.weatherapp.ui.nav.BottomNavItem
import org.albert.weatherapp.ui.nav.MainNavHost
import org.albert.weatherapp.ui.nav.Route
import org.albert.weatherapp.ui.theme.WeatherAppTheme
import org.albert.weatherapp.viewmodel.MainViewModel
import org.albert.weatherapp.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @SuppressLint("RestrictedApi")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showDialog by remember { mutableStateOf(false) }
            val fbDB = remember { FBDatabase() }
            val weatherService = remember { WeatherService() }
            val viewModel : MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB, weatherService)
            )
            // state hoisting
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val launcher = rememberLauncherForActivityResult(contract =
            ActivityResultContracts.RequestPermission(), onResult = {})

            WeatherAppTheme {
                if (showDialog) CityDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { city ->
                        if (city.isNotBlank()) viewModel.add(city)
                        showDialog = false
                    })
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val name = viewModel.user?.name ?: "[não logado]"
                                Text("Bem-vindo/a! $name")
                            },
                            actions = {
                                IconButton(onClick = {
                                    Firebase.auth.signOut()
                                    // finish() // removed on 5th practice
                                }) {
                                    Icon(
                                        imageVector =
                                        Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,
                        )
                        BottomNavBar(navController = navController, items)
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(navController = navController, viewModel)
                    }
                }
            }
        }
    }
}

