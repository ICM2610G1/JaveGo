package com.example.proyecto.Navigation

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.screens.AccountScreen
import com.example.proyecto.screens.ChatScreen
import com.example.proyecto.screens.CollectionScreen
import com.example.proyecto.screens.EditProfileScreen
import com.example.proyecto.screens.HomeScreen
import com.example.proyecto.screens.MapScreen
import com.example.proyecto.screens.MessagesScreen
import com.example.proyecto.screens.MissionsScreen
import com.example.proyecto.screens.ShopScreen
import com.example.proyecto.screens.TeamScreen
enum class AppScreens {
    HomeScreen,
    AccountScreen,
    CollectionScreen,
    MissionsScreen,
    MessagesScreen,
    ChatScreen,
    ShopScreen,
    MapScreen,

    TeamScreen,

    EditProfileScreen
}

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeScreen.name
    ) {
        composable(route = AppScreens.HomeScreen.name) {
            HomeScreen(navController)
        }
        composable(route = AppScreens.AccountScreen.name) {
            AccountScreen(navController)
        }
        composable(route = AppScreens.CollectionScreen.name) {
            CollectionScreen(navController)
        }
        composable(route = AppScreens.MissionsScreen.name){
            MissionsScreen(navController)
        }
        composable(route = AppScreens.MessagesScreen.name){
            MessagesScreen(navController)
        }
        composable(route = AppScreens.ChatScreen.name){
            ChatScreen(navController)
        }
        composable(route = AppScreens.ShopScreen.name){
            ShopScreen(navController)
        }
        composable(route = AppScreens.MapScreen.name){
            MapScreen(navController)
        }
        composable(route = AppScreens.TeamScreen.name){
            TeamScreen(navController)
        }
        composable(route = AppScreens.EditProfileScreen.name){
            EditProfileScreen((navController))
        }
    }
}