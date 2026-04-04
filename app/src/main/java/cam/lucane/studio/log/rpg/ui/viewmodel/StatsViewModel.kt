package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.entity.StatSection
import cam.lucane.studio.log.rpg.data.entity.StatWidget
import cam.lucane.studio.log.rpg.data.entity.WidgetAccentColor
import cam.lucane.studio.log.rpg.data.entity.WidgetType
import cam.lucane.studio.log.rpg.data.repository.StatRepository
import cam.lucane.studio.log.rpg.data.repository.StatSectionWithWidgets
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StatsUiState(
    val sections: List<StatSectionWithWidgets> = emptyList(),
    /** Id de la section en cours d'édition. Null = aucune. */
    val editingSectionId: Long? = null,
    val showAddSectionSheet: Boolean = false,
    val showAddWidgetSheet: Boolean = false,
    /** Section cible lors de l'ouverture du sheet d'ajout de widget. */
    val targetSectionId: Long? = null,
)

class StatsViewModel(
    private val characterId: Long,
    private val repository: StatRepository,
    application: Application,
) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _ui.asStateFlow()

    init {
        repository.getSectionsWithWidgets(characterId)
            .onEach { sections -> _ui.update { it.copy(sections = sections) } }
            .launchIn(viewModelScope)
    }

    // ── Édition ──────────────────────────────────────────────────────────────

    fun startEdit(sectionId: Long) = _ui.update { it.copy(editingSectionId = sectionId) }
    fun stopEdit()                 = _ui.update { it.copy(editingSectionId = null) }

    // ── Sheets ───────────────────────────────────────────────────────────────

    fun openAddSectionSheet()  = _ui.update { it.copy(showAddSectionSheet = true) }
    fun closeAddSectionSheet() = _ui.update { it.copy(showAddSectionSheet = false) }

    fun openAddWidgetSheet(sectionId: Long) =
        _ui.update { it.copy(showAddWidgetSheet = true, targetSectionId = sectionId) }

    fun closeAddWidgetSheet() =
        _ui.update { it.copy(showAddWidgetSheet = false, targetSectionId = null) }

    // ── Sections ─────────────────────────────────────────────────────────────

    fun addSection(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val position = _ui.value.sections.size
            repository.addSection(characterId, title, position)
            closeAddSectionSheet()
        }
    }

    fun updateSectionTitle(sectionId: Long, newTitle: String) {
        if (newTitle.isBlank()) return
        val section = _ui.value.sections.firstOrNull { it.section.id == sectionId }?.section ?: return
        viewModelScope.launch { repository.updateSection(section.copy(title = newTitle)) }
    }

    fun deleteSection(section: StatSection) {
        viewModelScope.launch {
            if (_ui.value.editingSectionId == section.id) stopEdit()
            repository.deleteSection(section)
        }
    }

    // ── Widgets ──────────────────────────────────────────────────────────────

    fun addWidget(title: String, type: WidgetType, accentColor: WidgetAccentColor) {
        val sectionId = _ui.value.targetSectionId ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            val position = _ui.value.sections
                .firstOrNull { it.section.id == sectionId }?.widgets?.size ?: 0
            repository.addWidget(sectionId, title, type, accentColor, position)
            closeAddWidgetSheet()
        }
    }

    fun updateWidget(widget: StatWidget) {
        viewModelScope.launch { repository.updateWidget(widget) }
    }

    fun deleteWidget(widget: StatWidget) {
        viewModelScope.launch { repository.deleteWidget(widget) }
    }

    // ── Factory ──────────────────────────────────────────────────────────────

    companion object {
        fun factory(characterId: Long, application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db         = LogRPGDatabase.getDatabase(application)
                    val repository = StatRepository(db.statDao())
                    return StatsViewModel(characterId, repository, application) as T
                }
            }
    }
}