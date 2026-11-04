package com.example.upcar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.upcar.data.Motorista
import com.example.upcar.data.repository.CaronaRepository
import com.example.upcar.data.repository.MensagemRepository
import com.example.upcar.data.repository.MotoristaRepository
import com.example.upcar.util.CAMPUS_LAT
import com.example.upcar.util.CAMPUS_LNG
import com.example.upcar.util.DESTINO_PADRAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class MotoristaUiState(
    val motoristas: List<Motorista> = emptyList(),
    val destinoBusca: String = "Campus Centro",
    val selecionadoId: Int? = null
) {
    val motoristasFiltrados: List<Motorista>
        get() = motoristas.filter {
            destinoBusca.isBlank() || it.destino.contains(destinoBusca.trim(), ignoreCase = true)
        }
}

class MotoristaViewModel(
    private val motoristaRepository: MotoristaRepository,
    private val caronaRepository: CaronaRepository,
    private val mensagemRepository: MensagemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MotoristaUiState())
    val uiState: StateFlow<MotoristaUiState> = _uiState.asStateFlow()

    private var proximoMotoristaId = 1

    init {
        viewModelScope.launch {
            motoristaRepository.popularIniciaisSeVazio()
        }
        viewModelScope.launch {
            motoristaRepository.buscarTodos().collect { lista ->
                proximoMotoristaId = maxOf(proximoMotoristaId, (lista.maxOfOrNull { it.id } ?: 0) + 1)
                _uiState.update { it.copy(motoristas = lista) }
            }
        }
    }

    fun onDestinoBuscaChange(texto: String) {
        _uiState.update { it.copy(destinoBusca = texto) }
    }

    fun selecionarMotorista(id: Int?) {
        _uiState.update { it.copy(selecionadoId = id) }
    }

    fun adicionarMotorista(
        nome: String,
        carro: String,
        placa: String,
        horario: String,
        preco: Double,
        origem: String,
        lat: Double? = null,
        lng: Double? = null
    ) {
        val id = proximoMotoristaId++
        val latFinal = lat ?: (CAMPUS_LAT + ((id * 37) % 21 - 10) * 0.0007)
        val lngFinal = lng ?: (CAMPUS_LNG + ((id * 53) % 21 - 10) * 0.0007)
        val novo = Motorista(id, nome, 5.0, horario, preco, 0, carro, placa, origem, DESTINO_PADRAO, latFinal, lngFinal)

        viewModelScope.launch {
            motoristaRepository.salvar(novo)
        }
    }

    fun removerMotorista(motorista: Motorista) {
        _uiState.update {
            it.copy(selecionadoId = if (it.selecionadoId == motorista.id) null else it.selecionadoId)
        }

        viewModelScope.launch {
            // As caronas (e mensagens) do motorista precisam ser lidas ANTES de apagar o motorista.
            val caronasDoMotorista = caronaRepository.buscarTodos().first()
                .filter { it.motoristaId == motorista.id }
            val idsCaronas = caronasDoMotorista.map { it.id }

            if (idsCaronas.isNotEmpty()) {
                mensagemRepository.removerPorCaronas(idsCaronas)
            }
            caronasDoMotorista.forEach { caronaRepository.remover(it) }
            motoristaRepository.remover(motorista)
        }
    }
}
