package cam.lucane.studio.log.rpg.data.repository

import com.google.gson.Gson
import cam.lucane.studio.log.rpg.data.dao.AbilityDao
import cam.lucane.studio.log.rpg.data.dao.CharacterDao
import cam.lucane.studio.log.rpg.data.dao.ItemDao
import cam.lucane.studio.log.rpg.data.entity.*
import cam.lucane.studio.log.rpg.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import cam.lucane.studio.log.rpg.data.dao.NoteDao
import cam.lucane.studio.log.rpg.utils.MultiQrCodeUtils
import com.google.gson.reflect.TypeToken

class CharacterRepository(
    private val characterDao: CharacterDao,
    private val abilityDao: AbilityDao,
    private val itemDao: ItemDao,
    private val noteDao: NoteDao
) {
    private val gson = Gson()

    // ========== CHARACTER CRUD ==========
    fun getAllCharacters(): Flow<List<Character>> = characterDao.getAllCharacters()

    fun getCharacterById(id: Long): Flow<Character?> = characterDao.getCharacterById(id)

    suspend fun createCharacter(name: String): Long {
        val character = Character(name = name)
        return characterDao.insertCharacter(character)
    }

    suspend fun updateCharacter(character: Character) {
        val updated = character.copy(updatedAt = System.currentTimeMillis())
        characterDao.updateCharacter(updated)
    }

    suspend fun deleteCharacter(character: Character) {
        characterDao.deleteCharacter(character)
    }

    // ========== PDF MANAGEMENT ==========
    suspend fun updatePdf(characterId: Long, pdfPath: String?) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(pdfPath = pdfPath))
    }

    // ========== HEALTH & MANA ==========
    suspend fun updateHealth(characterId: Long, current: Int, max: Int) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(currentHealth = current, maxHealth = max))
    }

    suspend fun updateTemporaryHealth(characterId: Long, temp: Int) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(temporaryHealth = temp))
    }

    suspend fun updateMana(characterId: Long, current: Int, max: Int) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(currentMana = current, maxMana = max))
    }

    suspend fun updateManaMode(characterId: Long, mode: ManaMode) {
        characterDao.updateManaMode(characterId, mode, System.currentTimeMillis())
    }

    // ========== SPELL SLOTS ==========

    suspend fun updateSpellSlots(characterId: Long, slots: List<SpellSlot>) {
        characterDao.updateSpellSlots(characterId, slots.toJson(), System.currentTimeMillis())
    }

    // ========== CURRENCY MANAGEMENT ==========
    suspend fun updateCurrencyMode(characterId: Long, mode: CurrencyMode) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(currencyMode = mode))
    }

    suspend fun addCredits(characterId: Long, amount: Int) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(credits = character.credits + amount))
    }

    suspend fun spendCredits(characterId: Long, amount: Int): Boolean {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return false

        if (character.credits >= amount) {
            updateCharacter(character.copy(credits = character.credits - amount))
            return true
        }
        return false
    }

    // ========== ABILITIES ==========
    fun getAbilities(characterId: Long): Flow<List<Ability>> =
        abilityDao.getAbilitiesByCharacter(characterId)

    suspend fun addAbility(characterId: Long, ability: Ability) {
        abilityDao.insertAbility(ability.copy(characterId = characterId))
    }

    suspend fun updateAbility(ability: Ability) {
        abilityDao.updateAbility(ability)
    }

    suspend fun deleteAbility(ability: Ability) {
        abilityDao.deleteAbility(ability)
    }

    // ========== ITEMS ==========
    fun getItems(characterId: Long): Flow<List<Item>> =
        itemDao.getItemsByCharacter(characterId)

    suspend fun addItem(characterId: Long, item: Item) {
        itemDao.insertItem(item.copy(characterId = characterId))
    }

    suspend fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }

    suspend fun updateNotes(characterId: Long, notes: String) {
        withContext(Dispatchers.IO) {
            characterDao.updateNotes(characterId, notes, System.currentTimeMillis())
        }
    }

    // ========== IMPORT/EXPORT ==========
    // Remplace l'ancienne exportCharacter — unifié avec exportCharacterJson
    suspend fun exportCharacter(characterId: Long, noNotes: Boolean = false): String {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return ""
        val abilities = abilityDao.getAbilitiesByCharacterOnce(characterId)
        val items = itemDao.getItemsByCharacterOnce(characterId)
        val notes = if (noNotes) emptyList() else noteDao.getNotesByCharacterOnce(characterId)

        return exportCharacterJson(
            character = if (noNotes) character.copy(notes = null) else character,
            abilities = abilities,
            items = items,
            notes = notes
        )
    }

    // Remplace l'ancienne importCharacter — unifié avec importCharacterFromExport
    suspend fun importCharacter(json: String): Long? {
        return try {
            val export = gson.fromJson(json, CharacterExport::class.java)
            importCharacterFromExport(export)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun exportAbilities(characterId: Long): String {
        val abilities = abilityDao.getAbilitiesByCharacterOnce(characterId)
        val export = AbilitiesImport(abilities = abilities.map { it.toExport() })
        return gson.toJson(export)
    }

    suspend fun importAbilities(characterId: Long, json: String): Boolean {
        return try {
            val import = try {
                gson.fromJson(json, AbilitiesImport::class.java)
            } catch (e: Exception) {
                val abilities = gson.fromJson(json, Array<AbilityExport>::class.java).toList()
                AbilitiesImport(abilities)
            }

            val abilities = import.abilities.map { it.toEntity(characterId) }
            abilityDao.insertAbilities(abilities)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun exportInventory(characterId: Long): String {
        val items = itemDao.getItemsByCharacterOnce(characterId)
        val export = InventoryImport(items = items.map { it.toExport() })
        return gson.toJson(export)
    }

    suspend fun importInventory(characterId: Long, json: String): Boolean {
        return try {
            val import = gson.fromJson(json, InventoryImport::class.java)
            val items = import.items.map { it.toEntity(characterId) }
            itemDao.insertItems(items)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ✅ Profile Image Management
    suspend fun updateProfileImage(characterId: Long, imageUri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Créer le dossier images s'il n'existe pas
                val imagesDir = File(context.filesDir, "character_images")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                // Nom de fichier unique par personnage
                val fileName = "profile_$characterId.jpg"
                val destinationFile = File(imagesDir, fileName)

                // Copier l'image depuis l'URI vers le stockage local
                context.contentResolver.openInputStream(imageUri)?.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Supprimer l'ancienne image si elle est différente
                val character = characterDao.getCharacterById(characterId).firstOrNull()
                character?.profileImagePath?.let { oldPath ->
                    val oldFile = File(oldPath)
                    if (oldFile != destinationFile && oldFile.exists()) {
                        oldFile.delete()
                    }
                }

                // Mettre à jour la base de données
                characterDao.updateProfileImage(
                    characterId,
                    destinationFile.absolutePath,
                    System.currentTimeMillis()
                )

                destinationFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun removeProfileImage(characterId: Long) {
        withContext(Dispatchers.IO) {
            try {
                // Récupérer le personnage pour avoir le chemin de l'image
                val character = characterDao.getCharacterById(characterId).firstOrNull()

                // Supprimer le fichier image
                character?.profileImagePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }

                // Mettre à jour la base de données
                characterDao.updateProfileImage(
                    characterId,
                    null,
                    System.currentTimeMillis()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ========== NOTES ==========
    fun getNotes(characterId: Long) = noteDao.getNotesByCharacter(characterId)

    suspend fun addNote(characterId: Long, title: String = "Nouvelle note"): Long {
        val note = Note(characterId = characterId, title = title)
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun exportCharacterJson(
        character: Character,
        abilities: List<Ability>,
        items: List<Item>,
        notes: List<Note>
    ): String {
        val slotsType = object : TypeToken<List<SpellSlot>>() {}.type
        val export = CharacterExport(
            name            = character.name,
            currentHealth   = character.currentHealth,
            maxHealth       = character.maxHealth,
            temporaryHealth = character.temporaryHealth,
            currentMana     = character.currentMana,
            maxMana         = character.maxMana,
            manaMode        = character.manaMode.name,
            spellSlots      = Gson().fromJson<List<SpellSlot>>(character.spellSlotsJson, slotsType)
                .filter { it.max > 0 },
            currencyMode    = character.currencyMode.name,
            credits         = character.credits,
            notes           = character.notes,
            abilities       = abilities.map { AbilityExport(it.name, it.description,
                it.cost, it.range, it.duration, it.category) },
            items           = items.map {
                ItemExport(
                    name         = it.name,
                    description  = it.description,
                    quantity     = it.quantity,
                    weight       = it.weight,
                    category     = it.category,
                    notes        = it.notes,
                    isConsumable = it.isConsumable,
                    isEquipped   = it.isEquipped
                )
            },
            notesList       = notes.map { NoteExport(it.title, it.content) }
        )
        return Gson().toJson(export)
    }

    // Fallback sans notes pour le QR trop lourd
    suspend fun exportCharacterJsonNoNotes(
        character: Character,
        abilities: List<Ability>,
        items: List<Item>
    ): String = exportCharacterJson(
        character.copy(notes = null), abilities, items, emptyList()
    )

    suspend fun importCharacterFromExport(export: CharacterExport): Long {
        val now = System.currentTimeMillis()
        val character = Character(
            name            = export.name,
            currentHealth   = export.currentHealth,
            maxHealth       = export.maxHealth,
            temporaryHealth = export.temporaryHealth,
            currentMana     = export.currentMana,
            maxMana         = export.maxMana,
            manaMode        = runCatching { ManaMode.valueOf(export.manaMode) }.getOrDefault(ManaMode.MANA),
            spellSlotsJson  = export.spellSlots?.let { Gson().toJson(it) } ?: SpellSlot.defaultJson(),
            currencyMode    = runCatching { CurrencyMode.valueOf(export.currencyMode) }.getOrDefault(CurrencyMode.SINGLE),
            credits         = export.credits,
            notes           = export.notes,
            createdAt       = now,
            updatedAt       = now
        )
        val characterId = characterDao.insertCharacter(character)

        // ← null-safe sur les listes (Gson peut les mettre à null)
        export.abilities?.forEach { a ->
            abilityDao.insertAbility(Ability(
                characterId = characterId,
                name        = a.name,
                description = a.description,
                cost        = a.cost,
                range       = a.range,
                duration    = a.duration,
                category    = a.category,
                notes       = a.notes
            ))
        }

        export.items?.forEach { i ->
            itemDao.insertItem(Item(
                characterId  = characterId,
                name         = i.name,
                description  = i.description,
                quantity     = i.quantity,
                weight       = i.weight,
                category     = i.category,
                notes        = i.notes,
                isConsumable = i.isConsumable,
                isEquipped   = i.isEquipped
            ))
        }

        export.notesList?.forEach { n ->
            noteDao.insertNote(Note(
                characterId = characterId,
                title       = n.title,
                content     = n.content,
                createdAt   = now,
                updatedAt   = now
            ))
        }

        return characterId
    }

    suspend fun getAbilitiesOnce(characterId: Long): List<Ability> =
        abilityDao.getAbilitiesByCharacterOnce(characterId)

    suspend fun getItemsOnce(characterId: Long): List<Item> =
        itemDao.getItemsByCharacterOnce(characterId)

    suspend fun getNotesOnce(characterId: Long): List<Note> =
        noteDao.getNotesByCharacterOnce(characterId)

    suspend fun importFromMultiQr(
        stats: MultiQrCodeUtils.StatsPayload,
        abilities: List<MultiQrCodeUtils.AbilityPayload>?,
        items: List<MultiQrCodeUtils.ItemPayload>?,
        notes: List<MultiQrCodeUtils.NotePayload>?
    ): Long {
        val now = System.currentTimeMillis()

        val character = Character(
            name           = stats.name,
            currentHealth  = stats.currentHealth,
            maxHealth      = stats.maxHealth,
            temporaryHealth = stats.temporaryHealth,
            currentMana    = stats.currentMana,
            maxMana        = stats.maxMana,
            manaMode       = runCatching { ManaMode.valueOf(stats.manaMode) }.getOrDefault(ManaMode.MANA),
            currencyMode   = runCatching { CurrencyMode.valueOf(stats.currencyMode) }.getOrDefault(CurrencyMode.SINGLE),
            credits        = stats.credits,
            notes          = stats.legacyNotes,
            spellSlotsJson = Gson().toJson(stats.spellSlots.map { SpellSlot(it.level, it.current, it.max) }),
            createdAt      = now,
            updatedAt      = now
        )
        val characterId = characterDao.insertCharacter(character)

        abilities?.forEach { a ->
            abilityDao.insertAbility(Ability(
                characterId = characterId,
                name        = a.name,
                description = a.description,
                cost        = a.cost,
                range       = a.range,
                duration    = a.duration,
                category    = a.category,
                notes       = a.notes
            ))
        }

        items?.forEach { i ->
            itemDao.insertItem(Item(
                characterId  = characterId,
                name         = i.name,
                description  = i.description,
                quantity     = i.quantity,
                weight       = i.weight,
                category     = i.category,
                notes        = i.notes,
                isConsumable = i.isConsumable,
                isEquipped   = i.isEquipped
            ))
        }

        notes?.forEach { n ->
            noteDao.insertNote(Note(
                characterId = characterId,
                title       = n.title,
                content     = n.content,
                createdAt   = now,
                updatedAt   = now
            ))
        }

        return characterId
    }
}

// Extension functions
private fun Ability.toExport() = AbilityExport(
    name = name,
    description = description,
    cost = cost,
    range = range,
    duration = duration,
    category = category,
    notes = notes
)

private fun AbilityExport.toEntity(characterId: Long) = Ability(
    characterId = characterId,
    name = name,
    description = description,
    cost = cost,
    range = range,
    duration = duration,
    category = category,
    notes = notes
)

private fun Item.toExport() = ItemExport(
    name = name,
    description = description,
    quantity = quantity,
    weight = weight,
    category = category,
    isEquipped = isEquipped,
    isConsumable = isConsumable,
    notes = notes
)

private fun ItemExport.toEntity(characterId: Long) = Item(
    characterId = characterId,
    name = name,
    description = description,
    quantity = quantity,
    weight = weight,
    category = category,
    isEquipped = isEquipped,
    isConsumable = isConsumable,
    notes = notes
)
