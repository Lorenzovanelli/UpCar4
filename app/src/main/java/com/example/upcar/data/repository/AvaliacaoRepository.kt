package com.example.upcar.data.repository

import com.example.upcar.data.AvaliacaoCorrida
import com.example.upcar.data.AvaliacaoDao
import kotlinx.coroutines.flow.Flow

class AvaliacaoRepository(private val avaliacaoDao: AvaliacaoDao) {

    fun buscarTodos(): Flow<List<AvaliacaoCorrida>> = avaliacaoDao.buscarTodos()

    suspend fun salvar(avaliacao: AvaliacaoCorrida) = avaliacaoDao.salvar(avaliacao)
}
