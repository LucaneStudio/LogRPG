package cam.lucane.studio.log.rpg.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stat_sections",
    foreignKeys = [ForeignKey(
        entity        = Character::class,
        parentColumns = ["id"],
        childColumns  = ["characterId"],
        onDelete      = ForeignKey.CASCADE
    )],
    indices = [Index("characterId")]
)
data class StatSection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val characterId: Long,
    val title: String,
    val position: Int = 0,
)
