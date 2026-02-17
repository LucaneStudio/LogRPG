package cam.lucane.studio.log.rpg.data.dao

import androidx.room.*
import cam.lucane.studio.log.rpg.data.entity.Character
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
}
