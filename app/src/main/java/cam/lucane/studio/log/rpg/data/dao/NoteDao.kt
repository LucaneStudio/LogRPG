package cam.lucane.studio.log.rpg.data.dao

import androidx.room.*
import cam.lucane.studio.log.rpg.data.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE characterId = :characterId ORDER BY updatedAt DESC")
    fun getNotesByCharacter(characterId: Long): Flow<List<Note>>

    @Insert
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE characterId = :characterId")
    suspend fun getNotesByCharacterOnce(characterId: Long): List<Note>
}