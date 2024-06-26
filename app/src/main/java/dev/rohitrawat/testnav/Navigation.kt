package dev.rohitrawat.testnav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.MainScreen.route){
        composable(Screens.MainScreen.route){
            MainPage(name = "Navigation", navController = navController)
        }
        composable(Screens.settingScreen.route){
            SettingPage(text = "Navigation")
        }
    }
}