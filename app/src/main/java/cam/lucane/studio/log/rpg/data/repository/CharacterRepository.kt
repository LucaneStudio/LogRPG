package cam.lucane.studio.log.rpg.data.repository

import android.content.Context
import android.net.Uri
import cam.lucane.studio.log.rpg.data.dao.*
import cam.lucane.studio.log.rpg.data.entity.*
import cam.lucane.studio.log.rpg.data.model.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.File

class CharacterRepository(
    private val characterDao : CharacterDao,
    private val abilityDao   : AbilityDao,
    private val itemDao      : ItemDao,
    private val noteDao      : NoteDao,
    private val statDao      : StatDao,       // ← ajout
) {
    private val gson = Gson()

    // ========== CHARACTER CRUD ==========

    fun getAllCharacters(): Flow<List<Character>> = characterDao.getAllCharacters()

    fun getCharacterById(id: Long): Flow<Character?> = characterDao.getCharacterById(id)

    suspend fun createCharacter(name: String): Long =
        characterDao.insertCharacter(Character(name = name))

    suspend fun updateCharacter(character: Character) =
        characterDao.updateCharacter(character.copy(updatedAt = System.currentTimeMillis()))

    suspend fun deleteCharacter(character: Character) =
        characterDao.deleteCharacter(character)

    // ========== PDF ==========

    suspend fun updatePdf(characterId: Long, pdfPath: String?) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(pdfPath = pdfPath))
    }

    // ========== HEALTH & MANA ==========

    suspend fun updateHealth(characterId: Long, current: Int, max: Int) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(currentHealth = current, maxHealth = max))
    }

    suspend fun updateTemporaryHealth(characterId: Long, temp: Int) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(temporaryHealth = temp))
    }

    suspend fun updateMana(characterId: Long, current: Int, max: Int) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(currentMana = current, maxMana = max))
    }

    suspend fun updateManaMode(characterId: Long, mode: ManaMode) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(manaMode = mode))
    }

    suspend fun updateSpellSlots(characterId: Long, slots: List<SpellSlot>) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        val json = gson.toJson(slots)
        updateCharacter(c.copy(spellSlotsJson = json))
    }

    // ========== CURRENCY ==========

    suspend fun updateCurrencyMode(characterId: Long, mode: CurrencyMode) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(currencyMode = mode))
    }

    suspend fun addCredits(characterId: Long, amount: Int) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(c.copy(credits = c.credits + amount))
    }

    suspend fun spendCredits(characterId: Long, amount: Int): Boolean {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return false
        if (c.credits < amount) return false
        updateCharacter(c.copy(credits = c.credits - amount))
        return true
    }

    // ========== ABILITIES ==========

    fun getAbilities(characterId: Long): Flow<List<Ability>> =
        abilityDao.getAbilitiesByCharacter(characterId)

    suspend fun addAbility(characterId: Long, ability: Ability) =
        abilityDao.insertAbility(ability.copy(characterId = characterId))

    suspend fun updateAbility(ability: Ability) = abilityDao.updateAbility(ability)

    suspend fun deleteAbility(ability: Ability) = abilityDao.deleteAbility(ability)

    // ========== ITEMS ==========

    fun getItems(characterId: Long): Flow<List<Item>> =
        itemDao.getItemsByCharacter(characterId)

    suspend fun addItem(characterId: Long, item: Item) =
        itemDao.insertItem(item.copy(characterId = characterId))

    suspend fun updateItem(item: Item) = itemDao.updateItem(item)

    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)

    // ========== NOTES ==========

    fun getNotes(characterId: Long): Flow<List<Note>> =
        noteDao.getNotesByCharacter(characterId)

    suspend fun addNote(characterId: Long, title: String): Long =
        noteDao.insertNote(Note(characterId = characterId, title = title))

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun updateNotes(characterId: Long, notes: String) =
        withContext(Dispatchers.IO) {
            characterDao.updateNotes(characterId, notes, System.currentTimeMillis())
        }

    // ========== PROFILE IMAGE ==========

    suspend fun updateProfileImage(characterId: Long, imageUri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val dir  = File(context.filesDir, "profiles").also { it.mkdirs() }
                val file = File(dir, "profile_$characterId.jpg")
                context.contentResolver.openInputStream(imageUri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                val path = file.absolutePath
                val c    = characterDao.getCharacterByIdOnce(characterId) ?: return@runCatching null
                updateCharacter(c.copy(profileImagePath = path))
                path
            }.getOrNull()
        }
    }

    suspend fun removeProfileImage(characterId: Long) {
        val c = characterDao.getCharacterByIdOnce(characterId) ?: return
        c.profileImagePath?.let { runCatching { File(it).delete() } }
        updateCharacter(c.copy(profileImagePath = null))
    }

    // ========== IMPORT / EXPORT ==========

    /** Export complet du personnage : stats, capacités, inventaire. */
    suspend fun exportCharacter(characterId: Long): String {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return ""
        val abilities = abilityDao.getAbilitiesByCharacterOnce(characterId)
        val items     = itemDao.getItemsByCharacterOnce(characterId)

        // ── Caractéristiques ───────────────────────────────────────────────────
        val sections  = statDao.getSectionsByCharacterOnce(characterId)
        val sectionIds = sections.map { it.id }
        val allWidgets = if (sectionIds.isEmpty()) emptyList()
        else statDao.getWidgetsBySectionIdsOnce(sectionIds)
        val widgetsBySection = allWidgets.groupBy { it.sectionId }

        val statsExport = sections.map { section ->
            StatSectionExport(
                title    = section.title,
                position = section.position,
                widgets  = widgetsBySection[section.id].orEmpty().map { w ->
                    StatWidgetExport(
                        title       = w.title,
                        type        = w.type,
                        value       = w.value,
                        modifier    = w.modifier,
                        accentColor = w.accentColor,
                        position    = w.position,
                    )
                },
            )
        }

        val export = CharacterExport(
            name          = character.name,
            currentHealth = character.currentHealth,
            maxHealth     = character.maxHealth,
            currentMana   = character.currentMana,
            maxMana       = character.maxMana,
            currencyMode  = character.currencyMode.name,
            credits       = character.credits,
            notes         = character.notes,
            abilities     = abilities.map { it.toExport() },
            items         = items.map { it.toExport() },
            stats         = statsExport,
        )
        return gson.toJson(export)
    }

    /** Import complet : crée un nouveau personnage avec toutes ses données. */
    suspend fun importCharacter(json: String): Long? {
        return try {
            val export      = gson.fromJson(json, CharacterExport::class.java)
            val characterId = createCharacter(export.name)
            val character   = characterDao.getCharacterByIdOnce(characterId) ?: return null

            updateCharacter(
                character.copy(
                    currentHealth = export.currentHealth,
                    maxHealth     = export.maxHealth,
                    currentMana   = export.currentMana,
                    maxMana       = export.maxMana,
                    currencyMode  = CurrencyMode.valueOf(export.currencyMode),
                    credits       = export.credits,
                    notes         = export.notes,
                )
            )

            abilityDao.insertAbilities(export.abilities.map { it.toEntity(characterId) })
            itemDao.insertItems(export.items.map { it.toEntity(characterId) })

            // ── Restauration des caractéristiques ─────────────────────────────
            export.stats.forEach { sectionExport ->
                val sectionId = statDao.insertSection(
                    StatSection(
                        characterId = characterId,
                        title       = sectionExport.title,
                        position    = sectionExport.position,
                    )
                )
                sectionExport.widgets.forEach { w ->
                    statDao.insertWidget(
                        StatWidget(
                            sectionId   = sectionId,
                            title       = w.title,
                            type        = w.type,
                            value       = w.value,
                            modifier    = w.modifier,
                            accentColor = w.accentColor,
                            position    = w.position,
                        )
                    )
                }
            }

            characterId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ── Import/export partiels (capacités & inventaire) ───────────────────────

    suspend fun exportAbilities(characterId: Long): String {
        val abilities = abilityDao.getAbilitiesByCharacterOnce(characterId)
        return gson.toJson(AbilitiesImport(abilities = abilities.map { it.toExport() }))
    }

    suspend fun importAbilities(characterId: Long, json: String): Boolean {
        return try {
            val import = try {
                gson.fromJson(json, AbilitiesImport::class.java)
            } catch (e: Exception) {
                AbilitiesImport(gson.fromJson(json, Array<AbilityExport>::class.java).toList())
            }
            abilityDao.insertAbilities(import.abilities.map { it.toEntity(characterId) })
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    suspend fun exportInventory(characterId: Long): String {
        val items = itemDao.getItemsByCharacterOnce(characterId)
        return gson.toJson(InventoryImport(items = items.map { it.toExport() }))
    }

    suspend fun importInventory(characterId: Long, json: String): Boolean {
        return try {
            val import = gson.fromJson(json, InventoryImport::class.java)
            itemDao.insertItems(import.items.map { it.toEntity(characterId) })
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }
}

// ── Extensions de mapping (privées au fichier) ────────────────────────────────

private fun Ability.toExport() = AbilityExport(
    name        = name,
    description = description,
    cost        = cost,
    range       = range,
    duration    = duration,
    damage      = damage,
    category    = category,
    notes       = notes,
)

private fun AbilityExport.toEntity(characterId: Long) = Ability(
    characterId = characterId,
    name        = name,
    description = description,
    cost        = cost,
    range       = range,
    duration    = duration,
    damage      = damage,
    category    = category,
    notes       = notes,
)

private fun Item.toExport() = ItemExport(
    name         = name,
    description  = description,
    quantity     = quantity,
    weight       = weight,
    category     = category,
    isEquipped   = isEquipped,
    isConsumable = isConsumable,
    notes        = notes,
)

private fun ItemExport.toEntity(characterId: Long) = Item(
    characterId  = characterId,
    name         = name,
    description  = description,
    quantity     = quantity,
    weight       = weight,
    category     = category,
    isEquipped   = isEquipped,
    isConsumable = isConsumable,
    notes        = notes,
)