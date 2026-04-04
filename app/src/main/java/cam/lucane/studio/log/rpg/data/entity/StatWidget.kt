package cam.lucane.studio.log.rpg.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stat_widgets",
    foreignKeys = [ForeignKey(
        entity        = StatSection::class,
        parentColumns = ["id"],
        childColumns  = ["sectionId"],
        onDelete      = ForeignKey.CASCADE
    )],
    indices = [Index("sectionId")]
)
data class StatWidget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sectionId: Long,
    val title: String,
    /** [WidgetType] sérialisé en String pour Room. */
    val type: String = WidgetType.FREE.name,
    val value: String = "",
    /** Modificateur saisi à la main — uniquement affiché pour [WidgetType.CAR_MOD]. */
    val modifier: String = "",
    /** [WidgetAccentColor] sérialisé en String pour Room. */
    val accentColor: String = WidgetAccentColor.PURPLE.name,
    val position: Int = 0,
) {
    fun widgetType(): WidgetType =
        runCatching { WidgetType.valueOf(type) }.getOrDefault(WidgetType.FREE)

    fun widgetAccentColor(): WidgetAccentColor =
        runCatching { WidgetAccentColor.valueOf(accentColor) }.getOrDefault(WidgetAccentColor.PURPLE)
}
