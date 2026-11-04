package com.example.upcar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MotoristaDao {
    @Query("SELECT * FROM motoristas")
    fun buscarTodos(): Flow<List<Motorista>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(motorista: Motorista)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarTodos(motoristas: List<Motorista>)

    @Update
    suspend fun atualizar(motorista: Motorista)

    @Delete
    suspend fun remover(motorista: Motorista)
}

@Dao
interface CaronaDao {
    @Query("SELECT * FROM caronas")
    fun buscarTodos(): Flow<List<Carona>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(carona: Carona)

    @Update
    suspend fun atualizar(carona: Carona)

    @Delete
    suspend fun remover(carona: Carona)
}

@Dao
interface MensagemDao {
    @Query("SELECT * FROM mensagens")
    fun buscarTodos(): Flow<List<Mensagem>>

    @Query("SELECT * FROM mensagens WHERE caronaId = :caronaId")
    fun buscarDaCarona(caronaId: Int): Flow<List<Mensagem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(mensagem: Mensagem)

    @Delete
    suspend fun remover(mensagem: Mensagem)

    @Query("DELETE FROM mensagens WHERE caronaId IN (:caronaIds)")
    suspend fun removerPorCaronas(caronaIds: List<Int>)
}

@Dao
interface PassageiroDao {
    @Query("SELECT * FROM passageiros WHERE id = 1 LIMIT 1")
    fun buscarPassageiro(): Flow<Passageiro?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(passageiro: Passageiro)
}

@Dao
interface AvaliacaoDao {
    @Query("SELECT * FROM avaliacoes")
    fun buscarTodos(): Flow<List<AvaliacaoCorrida>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(avaliacao: AvaliacaoCorrida)
}
