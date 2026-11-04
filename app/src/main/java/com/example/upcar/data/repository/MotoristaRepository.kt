package com.example.upcar.data.repository

import com.example.upcar.data.Motorista
import com.example.upcar.data.MotoristaDao
import com.example.upcar.data.motoristasIniciais
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MotoristaRepository(private val motoristaDao: MotoristaDao) {

    fun buscarTodos(): Flow<List<Motorista>> = motoristaDao.buscarTodos()

    suspend fun salvar(motorista: Motorista) = motoristaDao.salvar(motorista)

    suspend fun atualizar(motorista: Motorista) = motoristaDao.atualizar(motorista)

    suspend fun remover(motorista: Motorista) = motoristaDao.remover(motorista)

    /** Se a tabela estiver vazia, popula os motoristas iniciais do app. */
    suspend fun popularIniciaisSeVazio() {
        if (motoristaDao.buscarTodos().first().isEmpty()) {
            motoristaDao.salvarTodos(motoristasIniciais())
        }
    }
}
