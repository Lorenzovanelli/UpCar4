package com.example.upcar.data.repository

import com.example.upcar.data.Carona
import com.example.upcar.data.CaronaDao
import kotlinx.coroutines.flow.Flow

class CaronaRepository(private val caronaDao: CaronaDao) {

    fun buscarTodos(): Flow<List<Carona>> = caronaDao.buscarTodos()

    suspend fun salvar(carona: Carona) = caronaDao.salvar(carona)

    suspend fun atualizar(carona: Carona) = caronaDao.atualizar(carona)

    suspend fun remover(carona: Carona) = caronaDao.remover(carona)
}
