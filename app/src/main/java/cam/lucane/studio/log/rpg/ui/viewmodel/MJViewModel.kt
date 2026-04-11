package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.session.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MJViewModel(application: Application) : AndroidViewModel(application) {

    private val server        = MJServer()
    private val nsdAdvertiser = NsdAdvertiser(application)

    val players      : StateFlow<List<PlayerSlot>> = server.players
    val isRunning    : StateFlow<Boolean>           = server.isRunning

    private val _sessionConfig = MutableStateFlow<SessionConfig?>(null)
    val sessionConfig: StateFlow<SessionConfig?> = _sessionConfig.asStateFlow()

    init {
        players
            .onEach { CombatSessionState.sync(it) }
            .launchIn(viewModelScope)
    }

    fun startSession() {
        viewModelScope.launch {
            val config = server.prepare()
            server.startListening(viewModelScope)
            _sessionConfig.value = config
            nsdAdvertiser.start(config.port, config.token)
        }
    }

    fun stopSession() {
        server.stop()
        nsdAdvertiser.stop()
        _sessionConfig.value = null
    }

    fun kickPlayer(socketId: String) {
        server.kickPlayer(socketId)
    }

    override fun onCleared() {
        server.stop()
        nsdAdvertiser.stop()
        super.onCleared()
    }
}