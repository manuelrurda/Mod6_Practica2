package com.manuelrurda.mod6_practica2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.manuelrurda.mod6_practica2.screens.CarDetailsScreen
import com.manuelrurda.mod6_practica2.screens.CarListScreen
import com.manuelrurda.mod6_practica2.ui.theme.Mod6_Practica2Theme
import com.manuelrurda.mod6_practica2.viewmodels.CarsViewModel
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private val viewModel: CarsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {            Mod6_Practica2Theme {
                navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = CarsList
                ){
                    composable<CarsList> {
                        CarListScreen(viewModel) { id ->
                            navController.navigate(CarDetails(id))
                        }
                    }
                    composable<CarDetails> {
                        val args = it.toRoute<CarDetails>()
                        CarDetailsScreen(id = args.id, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Serializable
object CarsList

@Serializable
data class CarDetails(val id: Int)