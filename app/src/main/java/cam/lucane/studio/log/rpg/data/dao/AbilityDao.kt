package cam.lucane.studio.log.rpg.data.dao

import androidx.room.*
import cam.lucane.studio.log.rpg.data.entity.Ability
import kotlinx.coroutines.flow.Flow

@Dao
interface AbilityDao {
    @Query("SELECT * FROM abilities WHERE characterId = :characterId ORDER BY name ASC")
    fun getAbilitiesByCharacter(characterId: Long): Flow<List<Ability>>
    
    @Query("SELECT * FROM abilities WHERE characterId = :characterId")
    suspend fun getAbilitiesByCharacterOnce(characterId: Long): List<Ability>
    
    @Insert
    suspend fun insertAbility(ability: Ability): Long
    
    @Insert
    suspend fun insertAbilities(abilities: List<Ability>)
    
    @Update
    suspend fun updateAbility(ability: Ability)
    
    @Delete
    suspend fun deleteAbility(ability: Ability)
    
    @Query("DELETE FROM abilities WHERE characterId = :characterId")
    suspend fun deleteAllAbilitiesByCharacter(characterId: Long)
}
