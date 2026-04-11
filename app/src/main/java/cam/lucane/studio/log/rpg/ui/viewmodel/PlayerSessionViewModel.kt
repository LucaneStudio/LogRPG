package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import cam.lucane.studio.log.rpg.data.session.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerSessionViewModel(application: Application) : AndroidViewModel(application) {

    private val client        = PlayerClient()
    private val nsdDiscoverer = NsdDiscoverer(application)

    val isConnected: StateFlow<Boolean> = client.isConnected
    val wasKicked  : StateFlow<Boolean> = client.wasKicked

    private val _playerName = MutableStateFlow("")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    private val _sharedCharacter = MutableStateFlow<Character?>(null)
    val sharedCharacter: StateFlow<Character?> = _sharedCharacter.asStateFlow()

    private val _pendingConfig = MutableStateFlow<SessionConfig?>(null)
    val pendingConfig: StateFlow<SessionConfig?> = _pendingConfig.asStateFlow()

    // ── QR Code ───────────────────────────────────────────────────────────────
    fun onQRScanned(content: String): Boolean {
        val config = PlayerClient.parseQRCode(content) ?: return false
        _pendingConfig.value = config
        return true
    }

    // ── Code de session (NSD) ─────────────────────────────────────────────────
    /**
     * Lance la découverte réseau à partir d'un code 6 caractères.
     * [onResult] est appelé sur le Main thread : true si la session a été trouvée.
     */
    fun discoverByCode(code: String, onResult: (Boolean) -> Unit) {
        nsdDiscoverer.discover(
            code    = code,
            scope   = viewModelScope,
            onFound = { config ->
                _pendingConfig.value = config
                onResult(true)
            },
            onError = { onResult(false) },
        )
    }

    fun setPlayerName(name: String) {
        _playerName.value = name
    }

    fun connect(onResult: (Boolean) -> Unit) {
        val config = _pendingConfig.value ?: return onResult(false)
        viewModelScope.launch {
            val ok = client.connect(config, viewModelScope)
            onResult(ok)
        }
    }

    fun shareCharacter(character: Character) {
        _sharedCharacter.value = character
        client.switchCharacter(character.toPlayerInfo())
    }

    fun notifyStatsChanged(character: Character) {
        if (_sharedCharacter.value?.id == character.id) {
            client.sendUpdate(character.toPlayerInfo())
        }
    }

    fun disconnect() {
        client.disconnect()
        nsdDiscoverer.stop()
        _sharedCharacter.value = null
        _pendingConfig.value   = null
    }

    fun resetKick() {
        disconnect()
    }

    override fun onCleared() {
        client.disconnect()
        nsdDiscoverer.stop()
        super.onCleared()
    }

    private fun Character.toPlayerInfo() = PlayerInfo(
        playerName    = _playerName.value,
        characterName = name,
        currentHealth = currentHealth + temporaryHealth,
        maxHealth     = maxHealth,
        currentMana   = currentMana,
        maxMana       = maxMana,
        hasMana       = manaMode == ManaMode.MANA,
        spellSlotsJson = if (manaMode == ManaMode.SPELL_SLOTS) spellSlotsJson else null,
    )
}