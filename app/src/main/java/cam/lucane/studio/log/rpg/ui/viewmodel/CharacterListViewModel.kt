package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.repository.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharacterListViewModel(application: Application) : AndroidViewModel(application) {

    private val database   = LogRPGDatabase.getDatabase(application)
    private val repository = CharacterRepository(
        characterDao = database.characterDao(),
        abilityDao   = database.abilityDao(),
        itemDao      = database.itemDao(),
        noteDao      = database.noteDao(),
        statDao      = database.statDao(),   // ← ajout
    )

    val characters: StateFlow<List<Character>> = repository.getAllCharacters()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun createCharacter(name: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createCharacter(name)
            onCreated(id)
        }
    }

    fun deleteCharacter(character: Character) {
        viewModelScope.launch { repository.deleteCharacter(character) }
    }

    fun importCharacter(json: String, onSuccess: (Long) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val characterId = repository.importCharacter(json)
            if (characterId != null) onSuccess(characterId) else onError()
        }
    }
}