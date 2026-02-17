package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CharacterDetailViewModelFactory(
    private val application: Application,
    private val characterId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterDetailViewModel(application, characterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
