package cam.lucane.studio.log.rpg.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pont read-only entre MJViewModel (producteur) et CombatViewModel (consommateur).
 * MJViewModel pousse les mises à jour joueurs ici ; CombatViewModel observe passivement.
 * Aucune modification du protocole réseau existant.
 */
object CombatSessionState {

    private val _players = MutableStateFlow<List<PlayerSlot>>(emptyList())
    val players: StateFlow<List<PlayerSlot>> = _players.asStateFlow()

    fun sync(players: List<PlayerSlot>) {
        _players.value = players
    }

    fun clear() {
        _players.value = emptyList()
    }

    fun loadTestPlayers() {
        _players.value = listOf(
            PlayerSlot(
                socketId   = "socket_1",
                playerName = "Alice",
                info       = PlayerInfo(
                    playerName     = "Alice",
                    characterName  = "Aelindra",
                    currentHealth  = 40,
                    maxHealth      = 65,
                    currentMana    = 0,
                    maxMana        = 0,
                    hasMana        = false,
                    spellSlotsJson = null,
                ),
            ),
            PlayerSlot(
                socketId   = "socket_2",
                playerName = "Bob",
                info       = PlayerInfo(
                    playerName    = "Bob",
                    characterName = "Thorin",
                    currentHealth = 72,
                    maxHealth     = 80,
                    currentMana   = 4,
                    maxMana       = 10,
                    hasMana       = true,
                ),
            ),
        )
    }
}
