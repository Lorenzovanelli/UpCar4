package com.example.upcar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.upcar.data.repository.AvaliacaoRepository
import com.example.upcar.data.repository.CaronaRepository
import com.example.upcar.data.repository.MensagemRepository
import com.example.upcar.data.repository.MotoristaRepository
import com.example.upcar.data.repository.PassageiroRepository

class UpCarViewModelFactory(
    private val motoristaRepository: MotoristaRepository,
    private val caronaRepository: CaronaRepository,
    private val mensagemRepository: MensagemRepository,
    private val passageiroRepository: PassageiroRepository,
    private val avaliacaoRepository: AvaliacaoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MotoristaViewModel::class.java) ->
            MotoristaViewModel(motoristaRepository, caronaRepository, mensagemRepository) as T

        modelClass.isAssignableFrom(CaronaViewModel::class.java) ->
            CaronaViewModel(motoristaRepository, caronaRepository, mensagemRepository, avaliacaoRepository) as T

        modelClass.isAssignableFrom(PassageiroViewModel::class.java) ->
            PassageiroViewModel(passageiroRepository) as T

        else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
