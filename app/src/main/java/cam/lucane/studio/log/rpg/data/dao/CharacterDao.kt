package cam.lucane.studio.log.rpg.data.dao

import androidx.room.*
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY updatedAt DESC")
    fun getAllCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :characterId")
    fun getCharacterById(characterId: Long): Flow<Character?>

    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacterByIdOnce(characterId: Long): Character?

    @Insert
    suspend fun insertCharacter(character: Character): Long

    @Update
    suspend fun updateCharacter(character: Character)

    @Delete
    suspend fun deleteCharacter(character: Character)

    @Query("DELETE FROM characters WHERE id = :characterId")
    suspend fun deleteCharacterById(characterId: Long)

    @Query("UPDATE characters SET notes = :notes, updatedAt = :updatedAt WHERE id = :characterId")
    suspend fun updateNotes(characterId: Long, notes: String, updatedAt: Long)

    @Query("UPDATE characters SET profileImagePath = :imagePath, updatedAt = :updatedAt WHERE id = :characterId")
    suspend fun updateProfileImage(characterId: Long, imagePath: String?, updatedAt: Long)

    // ✨ Mode mana
    @Query("UPDATE characters SET manaMode = :mode, updatedAt = :updatedAt WHERE id = :characterId")
    suspend fun updateManaMode(characterId: Long, mode: ManaMode, updatedAt: Long)

    // ✨ Emplacements de sorts
    @Query("UPDATE characters SET spellSlotsJson = :json, updatedAt = :updatedAt WHERE id = :characterId")
    suspend fun updateSpellSlots(characterId: Long, json: String, updatedAt: Long)

    @Query("UPDATE characters SET temporaryHealth = :temp, updatedAt = :updatedAt WHERE id = :characterId")
    suspend fun updateTemporaryHealth(characterId: Long, temp: Int, updatedAt: Long)
}