package cam.lucane.studio.log.rpg.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // PDF path
    val pdfPath: String? = null,

    // Counters
    val currentHealth: Int = 0,
    val maxHealth: Int = 100,
    val currentMana: Int = 0,
    val maxMana: Int = 100,

    // Currency settings
    val currencyMode: CurrencyMode = CurrencyMode.SINGLE,

    // ✅ NOUVELLE STRUCTURE : Un seul champ crédits
    val credits: Int = 0
)

enum class CurrencyMode {
    SINGLE,      // Affichage direct en crédits/pièces
    BY_TEN,      // 1Pc=1, 1Pa=10, 1Po=100
    BY_HUNDRED   // 1Pc=1, 1Pa=100, 1Po=10000
}

// ✅ Extension pour convertir crédits → pièces affichables
data class CurrencyDisplay(
    val gold: Int,
    val silver: Int,
    val copper: Int
)

fun Character.getCurrencyDisplay(): CurrencyDisplay {
    return when (currencyMode) {
        CurrencyMode.SINGLE -> CurrencyDisplay(0, 0, credits)
        CurrencyMode.BY_TEN -> {
            val gold = credits / 100
            val remainder = credits % 100
            val silver = remainder / 10
            val copper = remainder % 10
            CurrencyDisplay(gold, silver, copper)
        }
        CurrencyMode.BY_HUNDRED -> {
            val gold = credits / 10000
            val remainder = credits % 10000
            val silver = remainder / 100
            val copper = remainder % 100
            CurrencyDisplay(gold, silver, copper)
        }
    }
}