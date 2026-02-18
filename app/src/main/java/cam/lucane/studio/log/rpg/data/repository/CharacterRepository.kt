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

class CharacterRepository(
    private val characterDao: CharacterDao,
    private val abilityDao: AbilityDao,
    private val itemDao: ItemDao
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

    suspend fun updateMana(characterId: Long, current: Int, max: Int) {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return
        updateCharacter(character.copy(currentMana = current, maxMana = max))
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
    suspend fun exportCharacter(characterId: Long): String {
        val character = characterDao.getCharacterByIdOnce(characterId) ?: return ""
        val abilities = abilityDao.getAbilitiesByCharacterOnce(characterId)
        val items = itemDao.getItemsByCharacterOnce(characterId)

        val export = CharacterExport(
            name = character.name,
            currentHealth = character.currentHealth,
            maxHealth = character.maxHealth,
            currentMana = character.currentMana,
            maxMana = character.maxMana,
            currencyMode = character.currencyMode.name,
            notes = character.notes,
            credits = character.credits,
            abilities = abilities.map { it.toExport() },
            items = items.map { it.toExport() }
        )

        return gson.toJson(export)
    }

    suspend fun importCharacter(json: String): Long? {
        return try {
            val export = gson.fromJson(json, CharacterExport::class.java)
            val characterId = createCharacter(export.name)

            val character = characterDao.getCharacterByIdOnce(characterId)?.copy(
                currentHealth = export.currentHealth,
                maxHealth = export.maxHealth,
                currentMana = export.currentMana,
                maxMana = export.maxMana,
                currencyMode = CurrencyMode.valueOf(export.currencyMode),
                credits = export.credits,
                notes = export.notes
            ) ?: return null

            updateCharacter(character)

            val abilities = export.abilities.map { it.toEntity(characterId) }
            abilityDao.insertAbilities(abilities)

            val items = export.items.map { it.toEntity(characterId) }
            itemDao.insertItems(items)

            characterId
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
