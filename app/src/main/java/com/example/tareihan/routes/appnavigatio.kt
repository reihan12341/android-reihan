package com.example.tareihan.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tareihan.dto.Schedule.ScheduleData
import com.example.tareihan.ui.screens.DashboardScreen
import com.example.tareihan.ui.screens.LoginScreen
import com.example.tareihan.ui.screens.RegisterScreen
import com.example.tareihan.ui.screens.ScheduleItem.ListScheduleScreen
import com.example.tareihan.ui.screens.auditors.CreateAuditorScreen
import com.example.tareihan.ui.screens.auditors.EditAuditorScreen
import com.example.tareihan.ui.screens.auditors.list_auditor
import com.example.tareihan.ui.screens.disposisi_irbanda.CreateDisposisi_IrbandaScreen
import com.example.tareihan.ui.screens.disposisi_irbanda.Editdisposisi_irbandaScreen
import com.example.tareihan.ui.screens.disposisi_irbanda.Listdisposisi_irbandaScreen
import com.example.tareihan.ui.screens.dumas.CreateDumasScreen
import com.example.tareihan.ui.screens.dumas.EditDumasScreen
import com.example.tareihan.ui.screens.dumas.list_dumas
import com.example.tareihan.ui.screens.surat_tugas.CreateSuratTugasScreen
import com.example.tareihan.ui.screens.surat_tugas.EditSuratTugasScreen
import com.example.tareihan.ui.screens.surat_tugas.list_surat_tugas
import com.example.tareihan.ui.screens.temuan.CreateTemuanScreen
import com.example.tareihan.ui.screens.temuan.EditTemuanScreen
import com.example.tareihan.ui.screens.temuan.ListTemuanScreen
import com.example.tareihan.ui.screens.unit_kerja.CreateUnitKerjaScreen
import com.example.tareihan.ui.screens.unit_kerja.EditUnitKerjaScreen
import com.example.tareihan.ui.screens.unit_kerja.list_unit_kerja
import com.example.tareihan.ui.screens.user.CreateUserScreen
import com.example.tareihan.ui.screens.user.EditUserScreen
import com.example.tareihan.ui.screens.user.ListUserScreen

@Composable
fun AppNavigation(navController: NavController) {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = routes.regis
    ) {
//        composable(Routes.login) { LoginScreen(navController) }
        composable(routes.create_auditor) {
            CreateAuditorScreen(navController)
        }
        composable(routes.list_auditor) {
            list_auditor(navController)
        }

        composable(routes.dashboard) {
            DashboardScreen(navController)
        }

        composable(
            route = "edit_auditor/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditAuditorScreen(navController, id = id)
        }

        composable(routes.login) { LoginScreen(navController) }
        composable(routes.create_surat_tugas) {
            CreateSuratTugasScreen(navController)
        }
        composable(routes.list_surat_tugas) {
            list_surat_tugas(navController)
        }

        composable(routes.dashboard) {
            DashboardScreen(navController)
        }
        composable(routes.regis) {
            RegisterScreen(navController)
        }

        composable(
            route = "edit_surat_tugas/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditSuratTugasScreen(navController, id = id)
        }


        composable(routes.create_dumas_route) {
            CreateDumasScreen(navController)
        }

        composable(routes.list_dumas) {
            list_dumas(navController)
        }

        composable(
            route = "edit_dumas/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditDumasScreen(navController, id = id)
        }
        composable(routes.list_temuan) {
            ListTemuanScreen(navController)
        }
        composable(routes.create_temuan) {
            CreateTemuanScreen(navController)
        }

        composable(
            route = "edit_temuan/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditTemuanScreen(navController, id = id)
        }
        composable(routes.list_unit_keja) {
            list_unit_kerja(navController)
        }
        composable(routes.create_unit_kerja) {
            CreateUnitKerjaScreen(navController)
        }

        composable(
            route = "edit_unit_kerja/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditUnitKerjaScreen(id.toString(), navController)

        }
        composable(routes.create_user) {
            CreateUserScreen(navController)
        }
        composable(routes.list_user) {
            ListUserScreen(navController)
        }


        composable(
            route = "edit_user/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditUserScreen(navController, id = id.toString())
        }
        composable(routes.create_dumas_route) {
            CreateDumasScreen(navController)
        }

        composable(routes.list_dumas) {
            list_dumas(navController)
        }
        composable(
            route = "edit_dumas/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditDumasScreen(navController, id = id)
        }

        composable(
            route = "edit_disposisi_irbanda/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            Editdisposisi_irbandaScreen(navController, id = id)
        }
        composable(
            route = "edit_dumas/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditDumasScreen(navController, id = id)
        }

        composable(routes.login) { LoginScreen(navController) }
        composable(routes.create_disposisi_irbanda) {
            CreateDisposisi_IrbandaScreen(navController)
        }
        composable(routes.list_disposisi_irbanda) {
            Listdisposisi_irbandaScreen(navController)
        }

        composable("login"){
            LoginScreen(navController)
        }

//        composable(routes.list) { LoginScreen(navController) }
        composable(routes.list_Scheduleltem) {
            ListScheduleScreen()
        }

    }
}


