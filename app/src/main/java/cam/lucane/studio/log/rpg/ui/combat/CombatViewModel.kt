package cam.lucane.studio.log.rpg.ui.combat

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import cam.lucane.studio.log.rpg.data.session.CombatSessionState
import cam.lucane.studio.log.rpg.data.session.PlayerSlot
import cam.lucane.studio.log.rpg.ui.combat.model.*
import kotlinx.coroutines.flow.*

class CombatViewModel : ViewModel() {

    private val gson = Gson()

    private val _state = MutableStateFlow(CombatState())
    val state: StateFlow<CombatState> = _state.asStateFlow()

    val sessionPlayers: StateFlow<List<PlayerSlot>> = CombatSessionState.players

    init {
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

//        loadTestCombatData()
//        CombatSessionState.loadTestPlayers()
//        loadTestSetupData()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = CombatViewModel() as T
        }
    }

    // ── Setup ────────────────────────────────────────────────────────────────

    fun addSessionPlayers(slots: List<PlayerSlot>) {
        val existing = _state.value.participants.mapNotNull { it.linkedSocketId }.toSet()
        val newPJs   = slots.filter { it.socketId !in existing }.mapNotNull { it.toParticipant() }
        _state.update { it.copy(participants = it.participants + newPJs) }
    }

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

    fun startCombat() = _state.update { s ->
        s.copy(
            isStarted       = true,
            round           = 1,
            playedThisRound = emptyList(),
            currentId       = s.firstActiveId(),
        )
    }

    // ── Tour ─────────────────────────────────────────────────────────────────

    fun nextTurn() = _state.update { s ->
        val currentId = s.currentId ?: return@update s
        val newPlayed = s.playedThisRound + currentId

        // Prochain actif = persona non joué avec la plus haute effectiveInitiative
        val playedSet  = newPlayed.toSet()
        val nextActive = s.participants
            .filter { it.status == CombatStatus.ACTIVE && it.id !in playedSet }
            .maxByOrNull { it.effectiveInitiative }

        return@update if (nextActive != null) {
            // Il reste des personas à jouer ce round
            s.copy(playedThisRound = newPlayed, currentId = nextActive.id)
        } else {
            // Fin de round → appliquer les bonus en attente puis passer au round suivant
            val afterPending = s.applyPendingBonuses()
            afterPending.copy(
                round           = s.round + 1,
                playedThisRound = emptyList(),
                currentId       = afterPending.firstActiveId(),
            )
        }
    }

    private fun CombatState.applyPendingBonuses() = copy(
        participants = participants.map { p ->
            if (p.hasPendingBonus)
                p.copy(initiativeBonus = p.initiativeBonus + p.pendingBonus, pendingBonus = 0)
            else p
        }
    )

    // ── Initiative ───────────────────────────────────────────────────────────

    fun updateInitiative(id: String, value: Int) = update(id) { copy(initiative = value) }

    /**
     * Applique un bonus/malus d'initiative.
     *
     * - Persona actif ou déjà joué ce round → pendingBonus (appliqué au prochain round)
     * - Persona non encore joué → immédiat, se re-trie parmi les non-joués
     *   sans jamais passer avant l'actif
     */
    fun addBonus(id: String, bonus: Int) {
        val s             = _state.value
        val hasPlayedOrIs = id == s.currentId || id in s.playedThisRound

        if (hasPlayedOrIs) {
            update(id) { copy(pendingBonus = bonus) }
        } else {
            update(id) { copy(initiativeBonus = bonus) }
        }
    }

    fun removeBonus(id: String) = update(id) { copy(initiativeBonus = 0, pendingBonus = 0) }

    // ── HP ───────────────────────────────────────────────────────────────────

    fun changeHp(id: String, delta: Int) = update(id) {
        if (isReadOnly) this
        else copy(currentHp = (currentHp + delta).coerceIn(0, maxHp))
    }

    // ── Conditions ───────────────────────────────────────────────────────────

    fun addCondition(id: String, condition: String) = update(id) {
        copy(conditions = (conditions + condition).distinct())
    }

    fun removeCondition(id: String, condition: String) = update(id) {
        copy(conditions = conditions - condition)
    }

    // ── Statut ───────────────────────────────────────────────────────────────

    fun setStatus(id: String, status: CombatStatus) = update(id) { copy(status = status) }

    // ── Helpers ──────────────────────────────────────────────────────────────

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

    private fun loadTestCombatData() {
        val participants = listOf(
            CombatParticipant(name = "Aelindra", type = ParticipantType.PJ,
                currentHp = 40, maxHp = 65, hasMana = false,
                spellSlots = listOf(SpellSlotDisplay(1,3,4), SpellSlotDisplay(2,1,2), SpellSlotDisplay(3,0,1)),
                initiative = 18, initiativeBonus = 2, conditions = listOf("Empoisonné"),
                avatarColor = Color(0xFF7C6AF7), avatarLetter = "A"),
            CombatParticipant(name = "Thorin", type = ParticipantType.PJ,
                currentHp = 72, maxHp = 80, hasMana = true, currentMana = 4, maxMana = 10,
                initiative = 14, avatarColor = Color(0xFF5DE8C1), avatarLetter = "T"),
            CombatParticipant(name = "Gobelin archer", type = ParticipantType.MONSTER,
                currentHp = 8, maxHp = 14, initiative = 12,
                avatarColor = Color(0xFFF7716A), avatarLetter = "G"),
            CombatParticipant(name = "Gobelin 2", type = ParticipantType.MONSTER,
                currentHp = 0, maxHp = 14, initiative = 9, status = CombatStatus.KO,
                avatarColor = Color(0xFFF7716A), avatarLetter = "G"),
            CombatParticipant(name = "Chef de guerre", type = ParticipantType.PNJ,
                currentHp = 30, maxHp = 45, initiative = 16, pendingBonus = 3,
                avatarColor = Color(0xFFF59E0B), avatarLetter = "C"),
        )
        // Simuler round 2 : Aelindra(20) a joué, Thorin(14) est actif
        val aelindra = participants.first { it.name == "Aelindra" }
        val thorin   = participants.first { it.name == "Thorin" }
        _state.value = CombatState(
            participants    = participants,
            isStarted       = true,
            round           = 2,
            playedThisRound = listOf(aelindra.id),
            currentId       = thorin.id,
        )
    }

    private fun loadTestSetupData() {
        val participants = listOf(
            CombatParticipant(
                name = "Aelindra", type = ParticipantType.PJ,
                currentHp = 40, maxHp = 65, initiative = 18,
                linkedSocketId = "socket_1",
                avatarColor = Color(0xFF7C6AF7), avatarLetter = "A",
            ),
            CombatParticipant(
                name = "Thorin", type = ParticipantType.PJ,
                currentHp = 72, maxHp = 80, initiative = 0,   // pas encore saisi
                linkedSocketId = "socket_2",
                avatarColor = Color(0xFF5DE8C1), avatarLetter = "T",
            ),
            CombatParticipant(
                name = "Gobelin archer", type = ParticipantType.MONSTER,
                currentHp = 14, maxHp = 14, initiative = 12,
                avatarColor = Color(0xFFF7716A), avatarLetter = "G",
            ),
        )
        _state.value = CombatState(
            isStarted       = false,   // ← ouvre sur le setup
            participants    = participants,
            playedThisRound = emptyList(),
            currentId       = null,
        )
    }
}