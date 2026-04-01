package cam.lucane.studio.log.rpg.ui.combat.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class CombatParticipant(
    val id             : String = UUID.randomUUID().toString(),
    val name           : String,
    val type           : ParticipantType,
    val linkedSocketId : String? = null,

    // Ressources
    val currentHp  : Int,
    val maxHp      : Int,
    val hasMana    : Boolean = false,
    val currentMana: Int = 0,
    val maxMana    : Int = 0,
    val spellSlots : List<SpellSlotDisplay> = emptyList(),

    // Initiative
    val initiative     : Int = 0,
    val initiativeBonus: Int = 0,  // bonus actif, appliqué au tri
    val pendingBonus   : Int = 0,  // bonus en attente → appliqué au début du prochain round

    // Combat
    val conditions: List<String> = emptyList(),
    val status    : CombatStatus = CombatStatus.ACTIVE,

    // Affichage
    val avatarColor : Color,
    val avatarLetter: String,
) {
    val isReadOnly         : Boolean get() = linkedSocketId != null
    val effectiveInitiative: Int     get() = initiative + initiativeBonus
    val hpPercent          : Float   get() = if (maxHp == 0) 0f else (currentHp / maxHp.toFloat()).coerceIn(0f, 1f)
    val manaPercent        : Float   get() = if (maxMana == 0) 0f else (currentMana / maxMana.toFloat()).coerceIn(0f, 1f)
    val hasBonus           : Boolean get() = initiativeBonus != 0
    val hasPendingBonus    : Boolean get() = pendingBonus != 0
}

data class SpellSlotDisplay(val level: Int, val remaining: Int, val max: Int)