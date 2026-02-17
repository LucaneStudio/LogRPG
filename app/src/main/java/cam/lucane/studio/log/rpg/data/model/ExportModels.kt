package cam.lucane.studio.log.rpg.data.model

// Export complet d'un personnage (sans PDF)
data class CharacterExport(
    val name: String,
    val currentHealth: Int,
    val maxHealth: Int,
    val currentMana: Int,
    val maxMana: Int,
    val currencyMode: String,
    val credits: Int,
    val abilities: List<AbilityExport>,
    val items: List<ItemExport>
)

// Export d'une capacité
data class AbilityExport(
    val name: String,
    val description: String,
    val cost: String? = null,
    val range: String? = null,
    val duration: String? = null,
    val category: String? = null,
    val notes: String? = null
)

// Export d'un objet
data class ItemExport(
    val name: String,
    val description: String,
    val quantity: Int = 1,
    val weight: String? = null,
    val category: String? = null,
    val isEquipped: Boolean = false,
    val isConsumable: Boolean = false,  // ✅ NOUVEAU
    val notes: String? = null
)

// Pour l'import de capacités seules (format flexible)
data class AbilitiesImport(
    val abilities: List<AbilityExport>
)

// Pour l'import d'inventaire seul
data class InventoryImport(
    val items: List<ItemExport>
)
