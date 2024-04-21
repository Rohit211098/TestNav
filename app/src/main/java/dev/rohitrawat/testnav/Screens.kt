package dev.rohitrawat.testnav

sealed class Screens(val route : String) {
    object MainScreen : Screens("main_screen")
    object settingScreen : Screens("setting_screen")
    object infoScreen : Screens("info_screen")

}