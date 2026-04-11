package cam.lucane.studio.log.rpg.data.template

import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor.*
import cam.lucane.studio.log.rpg.data.entity.WidgetType.*

/**
 * Catalogue des templates de caractéristiques disponibles.
 * Chaque template génère des sections + widgets pré-remplis via [StatsViewModel.applyTemplate].
 */
object CharacterTemplates {
    // ── D&D 5e / Pathfinder ───────────────────────────────────────────────────

    val dnd5e = CharacterTemplate(
        id          = "dnd5e",
        name        = "D&D 5e / Pathfinder",
        description = "6 caractéristiques avec valeur + modificateur, stats de combat et maîtrises.",
        emoji       = "⚔️",
        sections    = listOf(
            TemplateSectionDef("Caractéristiques", listOf(
                TemplateWidgetDef("FOR",  CAR_MOD, RED),
                TemplateWidgetDef("DEX",  CAR_MOD, GREEN),
                TemplateWidgetDef("CON",  CAR_MOD, ORANGE),
                TemplateWidgetDef("INT",  CAR_MOD, BLUE),
                TemplateWidgetDef("SAG",  CAR_MOD, PURPLE),
                TemplateWidgetDef("CHA",  CAR_MOD, YELLOW),
            )),
            TemplateSectionDef("Combat", listOf(
                TemplateWidgetDef("CA",        FREE, PURPLE),
                TemplateWidgetDef("Initiative", FREE, GREEN),
                TemplateWidgetDef("Vitesse",    FREE, BLUE),
                TemplateWidgetDef("Dés de vie", FREE, ORANGE),
            )),
            TemplateSectionDef("Maîtrise", listOf(
                TemplateWidgetDef("Bonus maîtrise", FREE, PURPLE),
                TemplateWidgetDef("Inspiration",    FREE, YELLOW),
            )),
        ),
    )

    // ── Call of Cthulhu (BRP) ─────────────────────────────────────────────────

    val callOfCthulhu = CharacterTemplate(
        id          = "coc",
        name        = "Call of Cthulhu",
        description = "Système BRP : 8 caractéristiques en pourcentage + attributs dérivés (sanité, magie…).",
        emoji       = "🐙",
        sections    = listOf(
            TemplateSectionDef("Caractéristiques", listOf(
                TemplateWidgetDef("FOR", PERCENT, RED),
                TemplateWidgetDef("CON", PERCENT, ORANGE),
                TemplateWidgetDef("TAI", PERCENT, PURPLE),
                TemplateWidgetDef("DEX", PERCENT, GREEN),
                TemplateWidgetDef("APP", PERCENT, YELLOW),
                TemplateWidgetDef("INT", PERCENT, BLUE),
                TemplateWidgetDef("POU", PERCENT, PURPLE),
                TemplateWidgetDef("ÉDU", PERCENT, ORANGE),
            )),
            TemplateSectionDef("Attributs dérivés", listOf(
                TemplateWidgetDef("Sanité",        FREE, BLUE),
                TemplateWidgetDef("Chance",        FREE, YELLOW),
                TemplateWidgetDef("Magie",         FREE, PURPLE),
                TemplateWidgetDef("Mouvement",     FREE, GREEN),
            )),
        ),
    )

    // ── Cyberpunk Red ─────────────────────────────────────────────────────────

    val cyberpunk = CharacterTemplate(
        id          = "cyberpunk",
        name        = "Cyberpunk Red",
        description = "10 stats avec modificateurs, survie urbaine et combat futuriste.",
        emoji       = "🤖",
        sections    = listOf(
            TemplateSectionDef("Stats", listOf(
                TemplateWidgetDef("INT",     CAR_MOD, BLUE),
                TemplateWidgetDef("REF",     CAR_MOD, GREEN),
                TemplateWidgetDef("DEX",     CAR_MOD, GREEN),
                TemplateWidgetDef("TECH",    CAR_MOD, ORANGE),
                TemplateWidgetDef("COOL",    CAR_MOD, PURPLE),
                TemplateWidgetDef("VOLONTÉ", CAR_MOD, YELLOW),
                TemplateWidgetDef("CHANCE",  CAR_MOD, YELLOW),
                TemplateWidgetDef("MOV",     CAR_MOD, GREEN),
                TemplateWidgetDef("CORPS",   CAR_MOD, RED),
                TemplateWidgetDef("EMP",     CAR_MOD, PURPLE),
            )),
            TemplateSectionDef("Combat", listOf(
                TemplateWidgetDef("Points de vie", FREE, RED),
                TemplateWidgetDef("Armure",        FREE, ORANGE),
                TemplateWidgetDef("Initiative",    FREE, GREEN),
            )),
        ),
    )

    // ── Anathème (custom) ─────────────────────────────────────────────────────

    val anatheme = CharacterTemplate(
        id          = "anatheme",
        name        = "Anathème",
        description = "5 caractéristiques en pourcentage (Physique, Mental, Social, Arcane, Foi) et stats de combat.",
        emoji       = "🔮",
        sections    = listOf(
            TemplateSectionDef("Caractéristiques", listOf(
                TemplateWidgetDef("Physique", PERCENT, RED),
                TemplateWidgetDef("Mental",   PERCENT, BLUE),
                TemplateWidgetDef("Social",   PERCENT, YELLOW),
                TemplateWidgetDef("Arcane",   PERCENT, PURPLE),
                TemplateWidgetDef("Foi",      PERCENT, ORANGE),
            )),
            TemplateSectionDef("Valeurs de Combat", listOf(
                TemplateWidgetDef("Point d'armure", FREE, ORANGE),
                TemplateWidgetDef("Initiative",     FREE, GREEN),
            )),
        ),
    )

    val all: List<CharacterTemplate> = listOf(dnd5e, callOfCthulhu, cyberpunk, anatheme)
}