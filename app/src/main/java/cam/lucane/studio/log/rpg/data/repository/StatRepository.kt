package cam.lucane.studio.log.rpg.data.repository

import cam.lucane.studio.log.rpg.data.dao.StatDao
import cam.lucane.studio.log.rpg.data.entity.StatSection
import cam.lucane.studio.log.rpg.data.entity.StatWidget
import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.data.entity.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Modèle agrégé sections + widgets, utilisé uniquement dans la couche UI.
 */
data class StatSectionWithWidgets(
    val section: StatSection,
    val widgets: List<StatWidget>,
)

class StatRepository(private val statDao: StatDao) {

    /**
     * Émet en temps réel la liste des sections avec leurs widgets pour un personnage.
     */
    fun getSectionsWithWidgets(characterId: Long): Flow<List<StatSectionWithWidgets>> =
        statDao.getSectionsByCharacter(characterId).flatMapLatest { sections ->
            if (sections.isEmpty()) return@flatMapLatest flowOf(emptyList())

            statDao.getWidgetsForSections(sections.map { it.id }).map { allWidgets ->
                val bySection = allWidgets.groupBy { it.sectionId }
                sections.map { s -> StatSectionWithWidgets(s, bySection[s.id].orEmpty()) }
            }
        }

    // ── Sections ─────────────────────────────────────────────────────────────

    suspend fun addSection(characterId: Long, title: String, position: Int): Long =
        statDao.insertSection(StatSection(characterId = characterId, title = title, position = position))

    suspend fun updateSection(section: StatSection) =
        statDao.updateSection(section)

    suspend fun deleteSection(section: StatSection) =
        statDao.deleteSection(section)

    // ── Widgets ──────────────────────────────────────────────────────────────

    suspend fun addWidget(
        sectionId: Long,
        title: String,
        type: WidgetType,
        accentColor: WidgetAccentColor,
        position: Int,
    ): Long = statDao.insertWidget(
        StatWidget(
            sectionId   = sectionId,
            title       = title,
            type        = type.name,
            accentColor = accentColor.name,
            position    = position,
        )
    )

    suspend fun updateWidget(widget: StatWidget) =
        statDao.updateWidget(widget)

    suspend fun deleteWidget(widget: StatWidget) =
        statDao.deleteWidget(widget)
}
