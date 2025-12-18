package dev.eamoretti.amorettiexchange.presentation.agent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.ChatMessage
import dev.eamoretti.amorettiexchange.data.model.ChatRequest
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.CambistaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgentChatViewModel(application: Application) : AndroidViewModel(application) {

    // --- CORRECCIÓN CRÍTICA ---
    // Instanciamos el servicio usando el contexto de la aplicación, ya que ApiClient lo requiere.
    private val service = ApiClient.getClient(application).create(CambistaService::class.java)

    // Lista de mensajes (Estado de la UI)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Texto del input
    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    init {
        // Mensaje de bienvenida inicial
        _messages.value = listOf(
            ChatMessage("Hola. Soy el Agente Amoretti. Puedo consultar transacciones, clientes y balances. ¿Qué necesitas?", false)
        )
    }

    fun onInputChange(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        // 1. Agregamos el mensaje del usuario a la lista visualmente
        val userMsg = ChatMessage(text, true)
        _messages.value = _messages.value + userMsg

        // Limpiamos el input y activamos carga
        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 2. Llamada a la API usando la instancia 'service' que creamos arriba
                val response = service.chatWithAgent(ChatRequest(text))

                if (response.isSuccessful && response.body()?.success == true) {
                    val replyText = response.body()?.reply ?: "El agente respondió vacío."
                    _messages.value = _messages.value + ChatMessage(replyText, false)
                } else {
                    val errorMsg = response.body()?.error ?: "Error del servidor."
                    _messages.value = _messages.value + ChatMessage("Error: $errorMsg", false)
                }

            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Error de conexión: ${e.localizedMessage}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}