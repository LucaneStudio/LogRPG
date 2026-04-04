package cam.lucane.studio.log.rpg.data.dao

import androidx.room.*
import cam.lucane.studio.log.rpg.data.entity.StatSection
import cam.lucane.studio.log.rpg.data.entity.StatWidget
import kotlinx.coroutines.flow.Flow

@Dao
interface StatDao {

    // ── Sections ─────────────────────────────────────────────────────────────

    @Query("SELECT * FROM stat_sections WHERE characterId = :characterId ORDER BY position ASC")
    fun getSectionsByCharacter(characterId: Long): Flow<List<StatSection>>

    @Insert
    suspend fun insertSection(section: StatSection): Long

    @Update
    suspend fun updateSection(section: StatSection)

    @Delete
    suspend fun deleteSection(section: StatSection)

    // ── Widgets ──────────────────────────────────────────────────────────────

    @Query("SELECT * FROM stat_widgets WHERE sectionId = :sectionId ORDER BY position ASC")
    fun getWidgetsBySection(sectionId: Long): Flow<List<StatWidget>>

    @Query("SELECT * FROM stat_widgets WHERE sectionId IN (:sectionIds) ORDER BY position ASC")
    fun getWidgetsForSections(sectionIds: List<Long>): Flow<List<StatWidget>>

    @Insert
    suspend fun insertWidget(widget: StatWidget): Long

    @Update
    suspend fun updateWidget(widget: StatWidget)

    @Delete
    suspend fun deleteWidget(widget: StatWidget)
}
