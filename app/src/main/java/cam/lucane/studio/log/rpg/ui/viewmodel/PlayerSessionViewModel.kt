package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import cam.lucane.studio.log.rpg.data.session.PlayerClient
import cam.lucane.studio.log.rpg.data.session.PlayerInfo
import cam.lucane.studio.log.rpg.data.session.SessionConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerSessionViewModel(application: Application) : AndroidViewModel(application) {

    private val client = PlayerClient()

    val isConnected: StateFlow<Boolean> = client.isConnected
    val wasKicked:   StateFlow<Boolean> = client.wasKicked

    private val _playerName = MutableStateFlow("")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    private val _sharedCharacter = MutableStateFlow<Character?>(null)
    val sharedCharacter: StateFlow<Character?> = _sharedCharacter.asStateFlow()

    private val _pendingConfig = MutableStateFlow<SessionConfig?>(null)
    val pendingConfig: StateFlow<SessionConfig?> = _pendingConfig.asStateFlow()

    fun onQRScanned(content: String): Boolean {
        val config = PlayerClient.parseQRCode(content) ?: return false
        _pendingConfig.value = config
        return true
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
        android.util.Log.d("PlayerSession", "🧙 shareCharacter → ${character.name}, playerName=${_playerName.value}")
        _sharedCharacter.value = character
        client.switchCharacter(character.toPlayerInfo())
    }

    // Appelé automatiquement quand les PV/Mana changent (depuis CharacterDetailViewModel)
    fun notifyStatsChanged(character: Character) {
        if (_sharedCharacter.value?.id == character.id) {
            client.sendUpdate(character.toPlayerInfo())
        }
    }

    fun disconnect() {
        client.disconnect()
        _sharedCharacter.value = null
        _pendingConfig.value = null
    }

    fun resetKick() {
        // Appelé après affichage du message de kick
        disconnect()
    }

    override fun onCleared() {
        client.disconnect()
        super.onCleared()
    }

    private fun Character.toPlayerInfo() = PlayerInfo(
        playerName = _playerName.value,
        characterName = name,
        currentHealth = currentHealth + temporaryHealth,
        maxHealth = maxHealth,
        currentMana = currentMana,
        maxMana = maxMana,
        hasMana = manaMode == ManaMode.MANA,
        spellSlotsJson = if (manaMode == ManaMode.SPELL_SLOTS) spellSlotsJson else null
    )
}