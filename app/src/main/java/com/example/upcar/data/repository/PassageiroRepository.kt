package com.example.upcar.data.repository

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import com.example.upcar.data.Passageiro
import com.example.upcar.data.PassageiroDao
import com.example.upcar.util.decodificarFoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
class PassageiroRepository(
    private val passageiroDao: PassageiroDao,
    private val context: Context
) {

    fun buscarPassageiro(): Flow<Passageiro?> = passageiroDao.buscarPassageiro()

    suspend fun salvar(passageiro: Passageiro) = passageiroDao.salvar(passageiro)

    /** Le e reduz a foto fora da thread principal. Retorna null se nao for possivel decodificar. */
    suspend fun carregarFoto(uri: String): ImageBitmap? = withContext(Dispatchers.IO) {
        decodificarFoto(context, Uri.parse(uri))
    }
}
