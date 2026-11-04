package com.example.upcar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.upcar.data.AppDatabase
import com.example.upcar.data.repository.AvaliacaoRepository
import com.example.upcar.data.repository.CaronaRepository
import com.example.upcar.data.repository.MensagemRepository
import com.example.upcar.data.repository.MotoristaRepository
import com.example.upcar.data.repository.PassageiroRepository
import com.example.upcar.ui.navegacao.AppNavigation
import com.example.upcar.ui.theme.UpcarTheme
import com.example.upcar.viewmodel.UpCarViewModelFactory
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        // Monta o banco, os Repositories (um por Entity) e a Factory que os entrega aos ViewModels.
        val database = AppDatabase.getDatabase(applicationContext)
        val factory = UpCarViewModelFactory(
            motoristaRepository = MotoristaRepository(database.motoristaDao()),
            caronaRepository = CaronaRepository(database.caronaDao()),
            mensagemRepository = MensagemRepository(database.mensagemDao()),
            passageiroRepository = PassageiroRepository(database.passageiroDao(), applicationContext),
            avaliacaoRepository = AvaliacaoRepository(database.avaliacaoDao())
        )

        enableEdgeToEdge()
        setContent {
            UpcarTheme {
                AppNavigation(factory)
            }
        }
    }
}
