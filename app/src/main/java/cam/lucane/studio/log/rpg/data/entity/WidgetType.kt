package cam.lucane.studio.log.rpg.data.entity

enum class WidgetType {
    /** Valeur + modificateur saisis manuellement (style DnD mais pas limité à DnD). */
    CAR_MOD,
    /** Valeur numérique libre, sans modificateur. */
    FREE,
    /** Pourcentage 0–100 affiché en anneau. */
    PERCENT,
}
