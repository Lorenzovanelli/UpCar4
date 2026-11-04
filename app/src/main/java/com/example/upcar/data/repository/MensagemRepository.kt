package com.example.upcar.data.repository

import com.example.upcar.data.Mensagem
import com.example.upcar.data.MensagemDao
import kotlinx.coroutines.flow.Flow

class MensagemRepository(private val mensagemDao: MensagemDao) {

    fun buscarTodos(): Flow<List<Mensagem>> = mensagemDao.buscarTodos()

    fun buscarDaCarona(caronaId: Int): Flow<List<Mensagem>> = mensagemDao.buscarDaCarona(caronaId)

    suspend fun salvar(mensagem: Mensagem) = mensagemDao.salvar(mensagem)

    suspend fun remover(mensagem: Mensagem) = mensagemDao.remover(mensagem)

    suspend fun removerPorCaronas(caronaIds: List<Int>) = mensagemDao.removerPorCaronas(caronaIds)
}
