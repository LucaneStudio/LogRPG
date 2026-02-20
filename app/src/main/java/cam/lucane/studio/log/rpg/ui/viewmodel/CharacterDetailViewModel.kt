package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.entity.*
import cam.lucane.studio.log.rpg.data.repository.CharacterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.net.Uri

class CharacterDetailViewModel(
    private val characterId: Long,
    private val repository: CharacterRepository,
    application: Application
) : AndroidViewModel(application) {

    private val database = LogRPGDatabase.getDatabase(application)

    val character: StateFlow<Character?> = repository.getCharacterById(characterId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val abilities: StateFlow<List<Ability>> = repository.getAbilities(characterId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val items: StateFlow<List<Item>> = repository.getItems(characterId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val notes: StateFlow<List<Note>> = repository.getNotes(characterId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // PDF
    fun updatePdf(pdfPath: String?) {
        viewModelScope.launch {
            repository.updatePdf(characterId, pdfPath)
        }
    }

    // Health
    fun updateHealth(current: Int, max: Int) {
        viewModelScope.launch {
            repository.updateHealth(characterId, current, max)
        }
    }

    // Mana
    fun updateMana(current: Int, max: Int) {
        viewModelScope.launch {
            repository.updateMana(characterId, current, max)
        }
    }

    fun updateManaMode(mode: ManaMode) {
        viewModelScope.launch {
            repository.updateManaMode(characterId, mode)
        }
    }

    //Spell Slots
    fun updateSpellSlots(slots: List<SpellSlot>) {
        viewModelScope.launch {
            repository.updateSpellSlots(characterId, slots)
        }
    }

    // Currency
    fun updateCurrencyMode(mode: CurrencyMode) {
        viewModelScope.launch {
            repository.updateCurrencyMode(characterId, mode)
        }
    }

    fun addCredits(amount: Int) {
        viewModelScope.launch {
            repository.addCredits(characterId, amount)
        }
    }

    fun spendCredits(amount: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.spendCredits(characterId, amount)
            onResult(success)
        }
    }

    // Abilities
    fun addAbility(ability: Ability) {
        viewModelScope.launch {
            repository.addAbility(characterId, ability)
        }
    }

    fun updateAbility(ability: Ability) {
        viewModelScope.launch {
            repository.updateAbility(ability)
        }
    }

    fun deleteAbility(ability: Ability) {
        viewModelScope.launch {
            repository.deleteAbility(ability)
        }
    }

    fun importAbilities(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importAbilities(characterId, json)
            onResult(success)
        }
    }

    fun exportAbilities(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportAbilities(characterId)
            onResult(json)
        }
    }

    // Items
    fun addItem(item: Item) {
        viewModelScope.launch {
            repository.addItem(characterId, item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun importInventory(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importInventory(characterId, json)
            onResult(success)
        }
    }

    fun exportInventory(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportInventory(characterId)
            onResult(json)
        }
    }

    // Character Export
    fun exportCharacter(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportCharacter(characterId)
            onResult(json)
        }
    }

    fun updateNotes(notes: String) {
        viewModelScope.launch {
            repository.updateNotes(characterId, notes)
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            repository.updateProfileImage(characterId, imageUri, getApplication())
        }
    }

    fun removeProfileImage() {
        viewModelScope.launch {
            repository.removeProfileImage(characterId)
        }
    }

    fun addNote(title: String = "Nouvelle note", onCreated: (Long) -> Unit = {}) {
        viewModelScope.launch { val id = repository.addNote(characterId, title); onCreated(id) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.updateNote(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }
}
