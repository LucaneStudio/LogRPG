package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.session.MJServer
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.data.session.SessionConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MJViewModel(application: Application) : AndroidViewModel(application) {

    private val server = MJServer()

    val players: StateFlow<List<PlayerSlot>> = server.players
    val isRunning: StateFlow<Boolean> = server.isRunning

    private val _sessionConfig = MutableStateFlow<SessionConfig?>(null)
    val sessionConfig: StateFlow<SessionConfig?> = _sessionConfig.asStateFlow()

    fun startSession() {
        viewModelScope.launch {
            val config = server.prepare()
            server.startListening(viewModelScope)
            _sessionConfig.value = config
        }
    }

    fun stopSession() {
        server.stop()
        _sessionConfig.value = null
    }

    fun kickPlayer(socketId: String) {
        server.kickPlayer(socketId)
    }

    override fun onCleared() {
        server.stop()
        super.onCleared()
    }
}