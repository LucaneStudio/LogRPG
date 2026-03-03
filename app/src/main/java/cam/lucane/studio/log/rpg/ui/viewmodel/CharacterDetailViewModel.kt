package cam.lucane.studio.log.rpg.ui.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cam.lucane.studio.log.rpg.data.LogRPGDatabase
import cam.lucane.studio.log.rpg.data.entity.*
import cam.lucane.studio.log.rpg.data.repository.CharacterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import cam.lucane.studio.log.rpg.ui.utils.QrCodeUtils
import cam.lucane.studio.log.rpg.utils.MultiQrCodeUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking

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

    fun updateTemporaryHealth(temp: Int) {
        viewModelScope.launch {
            repository.updateTemporaryHealth(characterId, temp.coerceAtLeast(0))
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

    fun generateQrBitmap(noNotes: Boolean = false): Bitmap? {
        var result: Bitmap? = null
        runBlocking {
            val json = repository.exportCharacter(characterId, noNotes)
            result = QrCodeUtils.generateQrBitmap(json)
        }
        return result
    }

    fun estimateQrSize(noNotes: Boolean = false): Int {
        return runBlocking {
            val json = repository.exportCharacter(characterId, noNotes)
            QrCodeUtils.estimateQrSize(json)
        }
    }

    // Sauvegarder l'image QR dans Photos
    fun saveQrToGallery(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val name = character.value?.name ?: "personnage"
            val filename = "LogRPG_${name}_${System.currentTimeMillis()}.png"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= 29)
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LogRPG")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            ) ?: return false
            context.contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            true
        } catch (e: Exception) { false }
    }

    fun exportCharacterJson(): String {
        // Appelle la suspend fun du repository de façon synchrone
        // car on est déjà dans un contexte de coroutine
        return runBlocking {
            repository.exportCharacterJson(
                character = character.value ?: return@runBlocking "{}",
                abilities = abilities.value,
                items = items.value,
                notes = notes.value
            )
        }
    }

    data class MultiQrPayloads(
        val stats: String,
        val abilities: String,
        val items: String,
        val notes: String
    )

    fun generateMultiQrPayloads(): MultiQrPayloads {
        return runBlocking {
            val character = character.value ?: return@runBlocking MultiQrPayloads("","","","")
            val abilities = repository.getAbilitiesOnce(characterId)
            val items     = repository.getItemsOnce(characterId)
            val notes     = repository.getNotesOnce(characterId)
            val slots: List<SpellSlot> = character.spellSlotsJson
                ?.let { json ->
                    Gson().fromJson(json, object : TypeToken<List<SpellSlot>>() {}.type) as? List<SpellSlot>
                } ?: emptyList()

            MultiQrPayloads(
                stats      = MultiQrCodeUtils.encodeStats(character, slots),
                abilities  = MultiQrCodeUtils.encodeAbilities(abilities),
                items      = MultiQrCodeUtils.encodeItems(items),
                notes      = MultiQrCodeUtils.encodeNotes(notes)
            )
        }
    }

    // shareQrImages — partager plusieurs bitmaps via la share sheet Android
    fun shareQrImages(context: Context, bitmaps: List<Bitmap>) {
        viewModelScope.launch {
            val uris = bitmaps.mapIndexedNotNull { i, bitmap ->
                val name = "LogRPG_${character.value?.name}_QR${i+1}_${System.currentTimeMillis()}.png"
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= 29)
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LogRPG")
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                ) ?: return@mapIndexedNotNull null
                context.contentResolver.openOutputStream(uri)?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                uri
            }
            if (uris.isEmpty()) return@launch
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/png"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Partager les QR codes"))
        }
    }
}
