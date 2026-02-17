package cam.lucane.studio.log.rpg.data.dao

import androidx.room.*
import cam.lucane.studio.log.rpg.data.entity.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE characterId = :characterId ORDER BY category ASC, name ASC")
    fun getItemsByCharacter(characterId: Long): Flow<List<Item>>
    
    @Query("SELECT * FROM items WHERE characterId = :characterId")
    suspend fun getItemsByCharacterOnce(characterId: Long): List<Item>
    
    @Insert
    suspend fun insertItem(item: Item): Long
    
    @Insert
    suspend fun insertItems(items: List<Item>)
    
    @Update
    suspend fun updateItem(item: Item)
    
    @Delete
    suspend fun deleteItem(item: Item)
    
    @Query("DELETE FROM items WHERE characterId = :characterId")
    suspend fun deleteAllItemsByCharacter(characterId: Long)
}
