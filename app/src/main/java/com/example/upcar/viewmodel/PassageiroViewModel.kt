package com.example.upcar.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.upcar.data.Passageiro
import com.example.upcar.data.repository.PassageiroRepository
import com.example.upcar.util.bairroValido
import com.example.upcar.util.emailValido
import com.example.upcar.util.nomeValido
import com.example.upcar.util.rgmValido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PassageiroUiState(
    val passageiro: Passageiro? = null,
    val nome: String = "",
    val rgm: String = "",
    val email: String = "",
    val bairro: String = "",
    val fotoUri: String? = null,
    val foto: ImageBitmap? = null,
    val tentouAvancar: Boolean = false
) {
    val erroNome: String? get() = if (tentouAvancar && !nomeValido(nome)) "Informe seu nome" else null
    val erroRgm: String? get() = if (tentouAvancar && !rgmValido(rgm)) "RGM inválido" else null
    val erroEmail: String? get() = if (tentouAvancar && !emailValido(email)) "E-mail inválido" else null
    val erroBairro: String? get() = if (tentouAvancar && !bairroValido(bairro)) "Informe o bairro" else null

    val formularioValido: Boolean
        get() = nomeValido(nome) && rgmValido(rgm) && emailValido(email) && bairroValido(bairro)
}

class PassageiroViewModel(
    private val passageiroRepository: PassageiroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassageiroUiState())
    val uiState: StateFlow<PassageiroUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            passageiroRepository.buscarPassageiro().collect { salvo ->
                _uiState.update { it.copy(passageiro = salvo) }
            }
        }
    }

    fun onNomeChange(valor: String) = _uiState.update { it.copy(nome = valor) }

    fun onRgmChange(valor: String) = _uiState.update { it.copy(rgm = valor) }

    fun onEmailChange(valor: String) = _uiState.update { it.copy(email = valor) }

    fun onBairroChange(valor: String) = _uiState.update { it.copy(bairro = valor) }

    fun selecionarFoto(uri: String) {
        _uiState.update { it.copy(fotoUri = uri) }

        viewModelScope.launch {
            val bitmap = passageiroRepository.carregarFoto(uri)

            _uiState.update { if (it.fotoUri == uri) it.copy(foto = bitmap) else it }
        }
    }

    fun avancar(aoConcluir: () -> Unit) {
        _uiState.update { it.copy(tentouAvancar = true) }

        val estado = _uiState.value
        if (!estado.formularioValido) return

        val passageiro = Passageiro(
            estado.nome.trim(),
            estado.rgm.trim(),
            estado.email.trim(),
            estado.bairro.trim(),
            estado.fotoUri
        )

        viewModelScope.launch {
            passageiroRepository.salvar(passageiro)
            aoConcluir()
        }
    }
}
