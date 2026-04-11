package cam.lucane.studio.log.rpg.ui.combat

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.repository.CharacterRepository
import cam.lucane.studio.log.rpg.data.session.CombatSessionState
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.ui.combat.model.*
import kotlinx.coroutines.flow.*

class CombatViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()

    // ── Base de données ───────────────────────────────────────────────────────
    private val db = LogRPGDatabase.getDatabase(application)
    private val repository = CharacterRepository(
        characterDao = db.characterDao(),
        abilityDao   = db.abilityDao(),
        itemDao      = db.itemDao(),
        noteDao      = db.noteDao(),
        statDao      = db.statDao(),
    )

    // ── États ─────────────────────────────────────────────────────────────────
    private val _state = MutableStateFlow(CombatState())
    val state: StateFlow<CombatState> = _state.asStateFlow()

    val sessionPlayers: StateFlow<List<PlayerSlot>> = CombatSessionState.players

    /** Liste des personnages locaux disponibles pour le combat. */
    val localCharacters: StateFlow<List<Character>> = repository.getAllCharacters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Factory ───────────────────────────────────────────────────────────────
    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CombatViewModel(application) as T
            }
    }

    init {
        // Sync des stats joueurs connectés en temps réel
        CombatSessionState.players
            .onEach { slots ->
                _state.update { s ->
                    s.copy(participants = s.participants.map { p ->
                        slots.find { it.socketId == p.linkedSocketId }
                            ?.let { p.syncFrom(it) } ?: p
                    })
                }
            }
            .launchIn(viewModelScope)
    }

    // ── Setup : joueurs de session ────────────────────────────────────────────

    fun addSessionPlayers(slots: List<PlayerSlot>) {
        val existing = _state.value.participants.mapNotNull { it.linkedSocketId }.toSet()
        val newPJs   = slots.filter { it.socketId !in existing }.mapNotNull { it.toParticipant() }
        _state.update { it.copy(participants = it.participants + newPJs) }
    }

    // ── Setup : personnages locaux ────────────────────────────────────────────

    /**
     * Ajoute un personnage local au combat en tant que PNJ.
     * Ses PV actuels sont repris ; il sera traité comme un PNJ en combat.
     */
    fun addLocalCharacter(character: Character) {
        // Garde-fou : ne pas ajouter deux fois le même
        if (_state.value.participants.any { it.localCharId == character.id }) return
        _state.update {
            it.copy(participants = it.participants + CombatParticipant(
                name         = character.name,
                type         = ParticipantType.PNJ,
                localCharId  = character.id,
                currentHp    = character.currentHealth,
                maxHp        = character.maxHealth,
                avatarColor  = colorFor(character.name),
                avatarLetter = character.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            ))
        }
    }

    // ── Setup : monstres / PNJ manuels ───────────────────────────────────────

    fun addParticipant(name: String, type: ParticipantType, maxHp: Int, initiative: Int) {
        _state.update {
            it.copy(participants = it.participants + CombatParticipant(
                name         = name,
                type         = type,
                currentHp    = maxHp,
                maxHp        = maxHp,
                initiative   = initiative,
                avatarColor  = colorFor(name),
                avatarLetter = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            ))
        }
    }

    fun removeParticipant(id: String) =
        _state.update { it.copy(participants = it.participants.filter { p -> p.id != id }) }

    // ── Démarrage ─────────────────────────────────────────────────────────────

    fun startCombat() = _state.update { s ->
        s.copy(
            isStarted       = true,
            round           = 1,
            playedThisRound = emptyList(),
            currentId       = s.firstActiveId(),
        )
    }

    // ── Tour ──────────────────────────────────────────────────────────────────

    fun nextTurn() = _state.update { s ->
        val currentId = s.currentId ?: return@update s
        val played    = s.playedThisRound + currentId
        val remaining = s.sortedActive.filter { it.id !in played }

        if (remaining.isEmpty()) {
            // Fin du round → nouveau round
            val allActive  = s.participants.filter { it.status == CombatStatus.ACTIVE }
            val promoted   = s.participants.map { p ->
                if (p.hasPendingBonus) p.copy(initiativeBonus = p.pendingBonus, pendingBonus = 0) else p
            }
            val sortedNext = promoted
                .filter { it.status == CombatStatus.ACTIVE }
                .sortedByDescending { it.effectiveInitiative }
            s.copy(
                participants    = promoted,
                round           = s.round + 1,
                playedThisRound = emptyList(),
                currentId       = sortedNext.firstOrNull()?.id,
            )
        } else {
            s.copy(playedThisRound = played, currentId = remaining.first().id)
        }
    }

    fun updateInitiative(id: String, value: Int) = update(id) { copy(initiative = value) }

    // ── Bonus d'initiative ────────────────────────────────────────────────────

    fun addBonus(id: String, bonus: Int) {
        val s             = _state.value
        val hasPlayedOrIs = id == s.currentId || id in s.playedThisRound
        if (hasPlayedOrIs) update(id) { copy(pendingBonus = bonus) }
        else               update(id) { copy(initiativeBonus = bonus) }
    }

    fun removeBonus(id: String) = update(id) { copy(initiativeBonus = 0, pendingBonus = 0) }

    // ── HP ────────────────────────────────────────────────────────────────────

    fun changeHp(id: String, delta: Int) = update(id) {
        if (isReadOnly) this
        else copy(currentHp = (currentHp + delta).coerceIn(0, maxHp))
    }

    // ── Conditions ────────────────────────────────────────────────────────────

    fun addCondition(id: String, condition: String) = update(id) {
        copy(conditions = (conditions + condition).distinct())
    }

    fun removeCondition(id: String, condition: String) = update(id) {
        copy(conditions = conditions - condition)
    }

    // ── Statut ────────────────────────────────────────────────────────────────

    fun setStatus(id: String, status: CombatStatus) = update(id) { copy(status = status) }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun update(id: String, block: CombatParticipant.() -> CombatParticipant) =
        _state.update { s ->
            s.copy(participants = s.participants.map { if (it.id == id) it.block() else it })
        }

    private fun CombatParticipant.syncFrom(slot: PlayerSlot): CombatParticipant {
        val info = slot.info ?: return this
        return copy(
            currentHp   = info.currentHealth,
            maxHp       = info.maxHealth,
            hasMana     = info.hasMana,
            currentMana = info.currentMana,
            maxMana     = info.maxMana,
            spellSlots  = parseSlots(info.spellSlotsJson),
        )
    }

    private fun PlayerSlot.toParticipant(): CombatParticipant? {
        val info = this.info ?: return null
        return CombatParticipant(
            name           = info.characterName,
            type           = ParticipantType.PJ,
            linkedSocketId = socketId,
            currentHp      = info.currentHealth,
            maxHp          = info.maxHealth,
            hasMana        = info.hasMana,
            currentMana    = info.currentMana,
            maxMana        = info.maxMana,
            spellSlots     = parseSlots(info.spellSlotsJson),
            avatarColor    = colorFor(info.characterName),
            avatarLetter   = info.characterName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
        )
    }

    private val palette = listOf(
        Color(0xFF7C6AF7), Color(0xFFF7716A), Color(0xFF5DE8C1),
        Color(0xFFF59E0B), Color(0xFF3B82F6), Color(0xFFEC4899),
    )
    private fun colorFor(name: String) = palette[Math.abs(name.hashCode()) % palette.size]

    private data class SlotJson(val level: Int, val remaining: Int, val max: Int)
    private fun parseSlots(json: String?): List<SpellSlotDisplay> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            gson.fromJson(json, Array<SlotJson>::class.java)
                .map { SpellSlotDisplay(it.level, it.remaining, it.max) }
        }.getOrDefault(emptyList())
    }
}