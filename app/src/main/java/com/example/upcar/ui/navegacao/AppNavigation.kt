package com.example.upcar.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.upcar.viewmodel.CaronaViewModel
import com.example.upcar.viewmodel.MotoristaViewModel
import com.example.upcar.viewmodel.PassageiroViewModel
import com.example.upcar.ui.telas.BoasVindas
import com.example.upcar.ui.telas.CadastroPassageiro
import com.example.upcar.ui.telas.Chat
import com.example.upcar.ui.telas.CorridaEmAndamento
import com.example.upcar.ui.telas.CorridaFinalizada
import com.example.upcar.ui.telas.PerfilMotorista
import com.example.upcar.ui.telas.MotoristasScreen

object Rotas {
    const val BOAS_VINDAS = "boasVindas"
    const val CADASTRO = "cadastro"
    const val MOTORISTAS = "motoristas"
    const val CHAT = "chat"
    const val EM_ANDAMENTO = "emAndamento"
    const val PERFIL_MOTORISTA = "perfilMotorista"
    const val CORRIDA_FINALIZADA = "corridaFinalizada"

    const val ARG_CARONA_ID = "caronaId"
    const val ARG_MOTORISTA_ID = "motoristaId"

    const val CHAT_ROTA = "$CHAT/{$ARG_CARONA_ID}"
    const val EM_ANDAMENTO_ROTA = "$EM_ANDAMENTO/{$ARG_CARONA_ID}"
    const val PERFIL_MOTORISTA_ROTA = "$PERFIL_MOTORISTA/{$ARG_MOTORISTA_ID}"
    const val CORRIDA_FINALIZADA_ROTA = "$CORRIDA_FINALIZADA/{$ARG_CARONA_ID}"

    fun chat(caronaId: Int) = "$CHAT/$caronaId"
    fun emAndamento(caronaId: Int) = "$EM_ANDAMENTO/$caronaId"
    fun perfilMotorista(motoristaId: Int) = "$PERFIL_MOTORISTA/$motoristaId"
    fun corridaFinalizada(caronaId: Int) = "$CORRIDA_FINALIZADA/$caronaId"
}

@Composable
fun AppNavigation(factory: ViewModelProvider.Factory) {
    val navController = rememberNavController()

    val motoristaViewModel: MotoristaViewModel = viewModel(factory = factory)
    val caronaViewModel: CaronaViewModel = viewModel(factory = factory)
    val passageiroViewModel: PassageiroViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Rotas.BOAS_VINDAS
    ) {
        composable(Rotas.BOAS_VINDAS) {
            BoasVindas(onEntrar = { navController.navigate(Rotas.CADASTRO) })
        }

        composable(Rotas.CADASTRO) {
            CadastroPassageiro(
                viewModel = passageiroViewModel,
                onVoltar = { navController.popBackStack() },
                onConcluir = {
                    navController.navigate(Rotas.MOTORISTAS) {
                        popUpTo(Rotas.BOAS_VINDAS) { inclusive = true }
                    }
                }
            )
        }

        composable(Rotas.MOTORISTAS) {
            MotoristasScreen(
                viewModel = motoristaViewModel,
                onSolicitar = { motorista ->
                    caronaViewModel.solicitarCarona(motorista) { carona ->
                        navController.navigate(Rotas.chat(carona.id)) { launchSingleTop = true }
                    }
                }
            )
        }

        composable(
            route = Rotas.CHAT_ROTA,
            arguments = listOf(navArgument(Rotas.ARG_CARONA_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val caronaId = backStackEntry.arguments?.getInt(Rotas.ARG_CARONA_ID) ?: -1
            Chat(
                viewModel = caronaViewModel,
                caronaId = caronaId,
                onVoltar = { navController.popBackStack() },
                onConfirmar = {
                    navController.navigate(Rotas.emAndamento(caronaId)) { launchSingleTop = true }
                }
            )
        }

        composable(
            route = Rotas.EM_ANDAMENTO_ROTA,
            arguments = listOf(navArgument(Rotas.ARG_CARONA_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val caronaId = backStackEntry.arguments?.getInt(Rotas.ARG_CARONA_ID) ?: -1
            CorridaEmAndamento(
                viewModel = caronaViewModel,
                caronaId = caronaId,
                onFinalizar = {
                    caronaViewModel.finalizarCorrida(caronaId)
                    navController.navigate(Rotas.corridaFinalizada(caronaId)) {
                        popUpTo(Rotas.EM_ANDAMENTO_ROTA) { inclusive = true }
                    }
                },
                onVerPerfil = { id ->
                    navController.navigate(Rotas.perfilMotorista(id)) { launchSingleTop = true }
                }
            )
        }

        composable(
            route = Rotas.PERFIL_MOTORISTA_ROTA,
            arguments = listOf(navArgument(Rotas.ARG_MOTORISTA_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val motoristaId = backStackEntry.arguments?.getInt(Rotas.ARG_MOTORISTA_ID) ?: -1
            PerfilMotorista(
                caronaViewModel = caronaViewModel,
                passageiroViewModel = passageiroViewModel,
                motoristaId = motoristaId,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(
            route = Rotas.CORRIDA_FINALIZADA_ROTA,
            arguments = listOf(navArgument(Rotas.ARG_CARONA_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val caronaId = backStackEntry.arguments?.getInt(Rotas.ARG_CARONA_ID) ?: -1
            CorridaFinalizada(
                viewModel = caronaViewModel,
                caronaId = caronaId,
                onConcluir = { navController.popBackStack(Rotas.MOTORISTAS, false) }
            )
        }
    }
}
