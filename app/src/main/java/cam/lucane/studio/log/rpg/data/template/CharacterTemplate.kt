package cam.lucane.studio.log.rpg.data.template

import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.data.entity.WidgetType

data class TemplateWidgetDef(
    val title      : String,
    val type       : WidgetType,
    val accentColor: WidgetAccentColor,
)

data class TemplateSectionDef(
    val title  : String,
    val widgets: List<TemplateWidgetDef>,
)

data class CharacterTemplate(
    val id         : String,
    val name       : String,
    val description: String,
    val emoji      : String,
    val sections   : List<TemplateSectionDef>,
)