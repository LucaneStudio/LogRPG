package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.repository.CharacterRepository

class CharacterDetailViewModelFactory(
    private val characterId : Long,
    private val application : Application,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterDetailViewModel::class.java)) {
            val database   = LogRPGDatabase.getDatabase(application)
            val repository = CharacterRepository(
                characterDao = database.characterDao(),
                abilityDao   = database.abilityDao(),
                itemDao      = database.itemDao(),
                noteDao      = database.noteDao(),
                statDao      = database.statDao(),   // ← ajout
            )
            @Suppress("UNCHECKED_CAST")
            return CharacterDetailViewModel(characterId, repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}