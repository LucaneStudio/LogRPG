package cam.lucane.studio.log.rpg.data.model

// ── Export complet d'un personnage ────────────────────────────────────────────
data class CharacterExport(
    val name          : String,
    val currentHealth : Int,
    val maxHealth     : Int,
    val currentMana   : Int,
    val maxMana       : Int,
    val currencyMode  : String,
    val credits       : Int,
    val notes         : String?                = null,
    val abilities     : List<AbilityExport>    = emptyList(),
    val items         : List<ItemExport>       = emptyList(),
    // ── Caractéristiques (sections + widgets) — default vide pour rétrocompat ──
    val stats         : List<StatSectionExport> = emptyList(),
)

// ── Caractéristiques ──────────────────────────────────────────────────────────
data class StatSectionExport(
    val title    : String,
    val position : Int,
    val widgets  : List<StatWidgetExport> = emptyList(),
)

data class StatWidgetExport(
    val title      : String,
    val type       : String,   // WidgetType.name
    val value      : String,
    val modifier   : String,
    val accentColor: String,   // WidgetAccentColor.name
    val position   : Int,
)

// ── Capacités ─────────────────────────────────────────────────────────────────
data class AbilityExport(
    val name        : String,
    val description : String,
    val cost        : String? = null,
    val range       : String? = null,
    val duration    : String? = null,
    val damage      : String? = null,
    val category    : String? = null,
    val notes       : String? = null,
)

// ── Inventaire ────────────────────────────────────────────────────────────────
data class ItemExport(
    val name         : String,
    val description  : String,
    val quantity     : Int     = 1,
    val weight       : String? = null,
    val category     : String? = null,
    val isEquipped   : Boolean = false,
    val isConsumable : Boolean = false,
    val notes        : String? = null,
)

// ── Imports partiels (inchangés) ──────────────────────────────────────────────
data class AbilitiesImport(val abilities: List<AbilityExport>)
data class InventoryImport(val items: List<ItemExport>)