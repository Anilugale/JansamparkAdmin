package com.vk.jansamparkadmin.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screens(val route: String, val icon: ImageVector?) {
    object Login : Screens("login", null)
    object Dashboard : Screens("dashboard", Icons.Outlined.PieChart)
    object VillageList : Screens("VillageList", Icons.Outlined.List)
    object ComplaintList : Screens("ComplaintList", Icons.Outlined.List)
    object ComplaintDetails : Screens("ComplaintDetails", Icons.Outlined.List)
}

@Composable
fun ShowNavGraph(name: String) {
    val navigator  = rememberNavController()

    NavHost(
        navController = navigator,
        startDestination = name
    ) {
        composable(Screens.Login.route) { LoginScreen(navigator) }
        composable(Screens.Dashboard.route) { Dashboard(navigator) }
        composable(Screens.VillageList.route) { VillageList(navigator) }
        composable(Screens.ComplaintList.route+"/{id}") { ComplaintList(name = it.arguments?.getString("id")!!,navigator) }
        composable(Screens.ComplaintDetails.route+"/{id}") {
            ComplaintDetails(id = it.arguments?.getString("id")!!,navigator) }
    }
}


