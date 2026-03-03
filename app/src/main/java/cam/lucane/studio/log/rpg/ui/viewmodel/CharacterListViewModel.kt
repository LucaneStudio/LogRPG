package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.model.CharacterExport
import cam.lucane.studio.log.rpg.data.repository.CharacterRepository
import cam.lucane.studio.log.rpg.utils.MultiQrCodeUtils
import com.google.gson.Gson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharacterListViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LogRPGDatabase.getDatabase(application)
    private val repository = CharacterRepository(
        database.characterDao(),
        database.abilityDao(),
        database.itemDao(),
        database.noteDao()
    )
    
    val characters: StateFlow<List<Character>> = repository.getAllCharacters()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun createCharacter(name: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createCharacter(name)
            onCreated(id)
        }
    }
    
    fun deleteCharacter(character: Character) {
        viewModelScope.launch {
            repository.deleteCharacter(character)
        }
    }
    
    fun importCharacter(json: String, onSuccess: (Long) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val characterId = repository.importCharacter(json)
            if (characterId != null) {
                onSuccess(characterId)
            } else {
                onError()
            }
        }
    }

    fun importCharacterFromExport(
        export: CharacterExport,
        onSuccess: (Long) -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val id = repository.importCharacterFromExport(export)
                onSuccess(id)
            } catch (e: Exception) {
                onError()
            }
        }
    }

    sealed class ImportResult {
        data class Success(val name: String) : ImportResult()
        object Error : ImportResult()
    }

    fun importFromMultiQr(
        stats: MultiQrCodeUtils.StatsPayload,
        abilities: List<MultiQrCodeUtils.AbilityPayload>?,
        items: List<MultiQrCodeUtils.ItemPayload>?,
        notes: List<MultiQrCodeUtils.NotePayload>?,
        onSuccess: (Long) -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val id = repository.importFromMultiQr(stats, abilities, items, notes)
                onSuccess(id)
            } catch (e: Exception) {
                onError()
            }
        }
    }
}
