package com.example.upcar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.upcar.data.AvaliacaoCorrida
import com.example.upcar.data.Carona
import com.example.upcar.data.Mensagem
import com.example.upcar.data.Motorista
import com.example.upcar.data.repository.AvaliacaoRepository
import com.example.upcar.data.repository.CaronaRepository
import com.example.upcar.data.repository.MensagemRepository
import com.example.upcar.data.repository.MotoristaRepository
import com.example.upcar.util.formatarReais
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun horaAgora(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

private const val DURACAO_SIMULACAO_MS = 60_000L
private const val TICK_SIMULACAO_MS = 500L
private const val ATRASO_RESPOSTA_MS = 1_500L

data class CaronaUiState(
    val motoristas: List<Motorista> = emptyList(),
    val caronas: List<Carona> = emptyList(),
    val mensagens: List<Mensagem> = emptyList(),
    // caronaId -> avaliacao do passageiro (uma por corrida).
    val avaliacoes: Map<Int, AvaliacaoCorrida> = emptyMap(),
    // caronaId -> progresso da corrida simulada (0f..1f).
    val progressoCorridas: Map<Int, Float> = emptyMap(),
    // Caronas cuja corrida ja foi finalizada (cada uma soma 1 corrida ao motorista uma unica vez).
    val corridasFinalizadas: Set<Int> = emptySet()
) {
    fun motoristaPorId(id: Int): Motorista? = motoristas.firstOrNull { it.id == id }

    fun caronaPorId(id: Int): Carona? = caronas.firstOrNull { it.id == id }

    fun mensagensDaCarona(caronaId: Int): List<Mensagem> =
        mensagens.filter { it.caronaId == caronaId }
}

class CaronaViewModel(
    private val motoristaRepository: MotoristaRepository,
    private val caronaRepository: CaronaRepository,
    private val mensagemRepository: MensagemRepository,
    private val avaliacaoRepository: AvaliacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaronaUiState())
    val uiState: StateFlow<CaronaUiState> = _uiState.asStateFlow()

    private var proximaCaronaId = 1
    private var proximaMensagemId = 1

    // Como o banco agora responde via Flow (de forma assincrona), estas chaves evitam que um
    // toque duplo repita uma acao antes de o estado confirmado chegar de volta do Room.
    private val acoesEmAndamento = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            motoristaRepository.buscarTodos().collect { lista ->
                _uiState.update { it.copy(motoristas = lista) }
            }
        }
        viewModelScope.launch {
            caronaRepository.buscarTodos().collect { lista ->
                proximaCaronaId = maxOf(proximaCaronaId, (lista.maxOfOrNull { it.id } ?: 0) + 1)
                _uiState.update { it.copy(caronas = lista) }
            }
        }
        viewModelScope.launch {
            mensagemRepository.buscarTodos().collect { lista ->
                proximaMensagemId = maxOf(proximaMensagemId, (lista.maxOfOrNull { it.id } ?: 0) + 1)
                _uiState.update { it.copy(mensagens = lista) }
            }
        }
        viewModelScope.launch {
            avaliacaoRepository.buscarTodos().collect { lista ->
                _uiState.update {
                    it.copy(
                        avaliacoes = lista.associateBy { avaliacao -> avaliacao.caronaId },
                        corridasFinalizadas = it.corridasFinalizadas + lista.map { avaliacao -> avaliacao.caronaId }
                    )
                }
            }
        }
    }

    /**
     * Cria a carona (ou reaproveita uma pendente do mesmo motorista) e chama [aoCriar] quando
     * ela ja esta disponivel no estado, para a tela de chat nunca abrir antes dos dados.
     */
    fun solicitarCarona(motorista: Motorista, aoCriar: (Carona) -> Unit) {
        val pendente = _uiState.value.caronas
            .firstOrNull { it.motoristaId == motorista.id && !it.confirmada }
        if (pendente != null) {
            aoCriar(pendente)
            return
        }

        val chave = "solicitar-${motorista.id}"
        if (!acoesEmAndamento.add(chave)) return

        val carona = Carona(
            id = proximaCaronaId++,
            motoristaId = motorista.id,
            origem = motorista.origem,
            destino = motorista.destino,
            horario = motorista.horario,
            valor = motorista.preco
        )
        val msgInicial = Mensagem(
            id = proximaMensagemId++,
            caronaId = carona.id,
            texto = "Olá! Tudo bem? Vi que você pediu a carona para o ${motorista.destino}.",
            horario = horaAgora(),
            minha = false
        )

        viewModelScope.launch {
            try {
                caronaRepository.salvar(carona)
                mensagemRepository.salvar(msgInicial)
                _uiState.first { estado -> estado.caronaPorId(carona.id) != null }
                aoCriar(carona)
            } finally {
                acoesEmAndamento.remove(chave)
            }
        }
    }

    fun enviarMensagem(caronaId: Int, texto: String, minha: Boolean = true) {
        val mensagem = Mensagem(proximaMensagemId++, caronaId, texto, horaAgora(), minha)

        viewModelScope.launch {
            mensagemRepository.salvar(mensagem)
        }
    }

    fun removerMensagem(mensagem: Mensagem) {
        viewModelScope.launch {
            mensagemRepository.remover(mensagem)
        }
    }

    fun enviarContraproposta(caronaId: Int, valor: Double) {
        val carona = _uiState.value.caronaPorId(caronaId) ?: return
        if (carona.confirmada || carona.aguardandoResposta) return

        val chave = "contraproposta-$caronaId"
        if (!acoesEmAndamento.add(chave)) return

        enviarMensagem(caronaId, "Que tal ${formatarReais(valor)}?")

        viewModelScope.launch {
            try {
                caronaRepository.salvar(carona.copy(valor = valor, aguardandoResposta = true))

                delay(ATRASO_RESPOSTA_MS)
                val atual = _uiState.value.caronaPorId(caronaId)
                if (atual != null) {
                    caronaRepository.salvar(atual.copy(aguardandoResposta = false))
                    enviarMensagem(caronaId, "Combinado! Pode ser ${formatarReais(valor)}.", minha = false)
                }
            } finally {
                acoesEmAndamento.remove(chave)
            }
        }
    }

    fun confirmarCarona(caronaId: Int) {
        val carona = _uiState.value.caronaPorId(caronaId) ?: return
        if (carona.confirmada || carona.aguardandoResposta) return

        // Uma carona so pode ser confirmada uma vez: a chave nao precisa ser liberada.
        if (!acoesEmAndamento.add("confirmar-$caronaId")) return

        enviarMensagem(caronaId, "Fechado! Já aceito o valor de ${formatarReais(carona.valor)}.")

        viewModelScope.launch {
            caronaRepository.salvar(carona.copy(confirmada = true))
        }
    }

    fun iniciarCorrida(caronaId: Int) {
        if (_uiState.value.progressoCorridas.containsKey(caronaId)) return
        atualizarProgresso(caronaId, 0f)

        viewModelScope.launch {
            val passos = (DURACAO_SIMULACAO_MS / TICK_SIMULACAO_MS).toInt()
            for (i in 1..passos) {
                delay(TICK_SIMULACAO_MS)
                atualizarProgresso(caronaId, i / passos.toFloat())
            }
        }
    }

    private fun atualizarProgresso(caronaId: Int, progresso: Float) {
        _uiState.update { it.copy(progressoCorridas = it.progressoCorridas + (caronaId to progresso)) }
    }

    // Idempotente: finalizar a mesma corrida de novo não soma corridas em dobro.
    fun finalizarCorrida(caronaId: Int) {
        registrarFinalizacao(caronaId)
    }

    /** Soma 1 corrida ao motorista. Retorna o motorista atualizado, ou null se nada foi feito. */
    private fun registrarFinalizacao(caronaId: Int): Motorista? {
        val estado = _uiState.value
        if (caronaId in estado.corridasFinalizadas) return null
        val carona = estado.caronaPorId(caronaId) ?: return null
        val motorista = estado.motoristaPorId(carona.motoristaId) ?: return null

        _uiState.update { it.copy(corridasFinalizadas = it.corridasFinalizadas + caronaId) }
        val motoristaAtualizado = motorista.copy(corridas = motorista.corridas + 1)

        viewModelScope.launch {
            motoristaRepository.atualizar(motoristaAtualizado)
        }
        return motoristaAtualizado
    }

    // Idempotente: uma avaliação por corrida.
    fun avaliarCorrida(caronaId: Int, nota: Int, comentario: String, elogios: List<String>) {
        val motoristaFinalizado = registrarFinalizacao(caronaId)

        val estado = _uiState.value
        if (estado.avaliacoes.containsKey(caronaId)) return
        val carona = estado.caronaPorId(caronaId) ?: return
        val motorista = motoristaFinalizado ?: estado.motoristaPorId(carona.motoristaId) ?: return

        // Uma avaliacao por corrida: a chave nao precisa ser liberada.
        if (!acoesEmAndamento.add("avaliar-$caronaId")) return

        val notaLimpa = nota.coerceIn(1, 5)
        val avaliacao = AvaliacaoCorrida(caronaId, notaLimpa, comentario, elogios)

        val base = motorista.corridas - 1
        val novaNota = ((motorista.nota * base + notaLimpa) / (base + 1)).coerceIn(1.0, 5.0)
        val motoristaAtualizado = motorista.copy(nota = novaNota)

        viewModelScope.launch {
            avaliacaoRepository.salvar(avaliacao)
            motoristaRepository.atualizar(motoristaAtualizado)
        }
    }
}
