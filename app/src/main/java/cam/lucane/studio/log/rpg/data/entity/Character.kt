package cam.lucane.studio.log.rpg.data.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    val pdfPath: String? = null,

    // Compteurs
    val currentHealth: Int = 0,
    val maxHealth: Int = 100,
    val temporaryHealth: Int = 0,
    val currentMana: Int = 0,
    val maxMana: Int = 100,

    // ✨ Mode du compteur Mana/Sorts
    val manaMode: ManaMode = ManaMode.MANA,

    // ✨ Emplacements de sorts stockés en JSON
    val spellSlotsJson: String = SpellSlot.defaultJson(),

    val notes: String? = null,
    val profileImagePath: String? = null,

    val currencyMode: CurrencyMode = CurrencyMode.SINGLE,
    val credits: Int = 0
)

// ── Mana mode ──────────────────────────────────────────────────────────────

enum class ManaMode {
    MANA,        // Barre classique avec +/−
    SPELL_SLOTS  // Grille 3×3 des emplacements de sorts
}

// ── Spell slots ────────────────────────────────────────────────────────────

data class SpellSlot(
    @SerializedName("level")   val level: Int,
    @SerializedName("current") val current: Int,
    @SerializedName("max")     val max: Int
) {
    val isActive get() = max > 0
    val isDepleted get() = isActive && current == 0

    companion object {
        fun defaultJson(): String =
            Gson().toJson((1..9).map { SpellSlot(level = it, current = 0, max = 0) })
    }
}

fun Character.getSpellSlots(): List<SpellSlot> = try {
    Gson().fromJson(spellSlotsJson, Array<SpellSlot>::class.java).toList()
} catch (e: Exception) {
    Log.e("SpellSlots", "Parse FAILED", e)
    (1..9).map { SpellSlot(it, 0, 0) }
}

fun List<SpellSlot>.toJson(): String = Gson().toJson(this)

// ── Currency ───────────────────────────────────────────────────────────────

enum class CurrencyMode {
    SINGLE,
    BY_TEN,
    BY_HUNDRED
}

data class CurrencyDisplay(val gold: Int, val silver: Int, val copper: Int)

fun Character.getCurrencyDisplay(): CurrencyDisplay = when (currencyMode) {
    CurrencyMode.SINGLE     -> CurrencyDisplay(0, 0, credits)
    CurrencyMode.BY_TEN     -> CurrencyDisplay(credits / 100, (credits % 100) / 10, credits % 10)
    CurrencyMode.BY_HUNDRED -> CurrencyDisplay(credits / 10000, (credits % 10000) / 100, credits % 100)
}