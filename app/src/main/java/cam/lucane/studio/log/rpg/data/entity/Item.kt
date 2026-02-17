package cam.lucane.studio.log.rpg.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("characterId")]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val characterId: Long,
    val name: String,
    val description: String,
    val quantity: Int = 1,
    val weight: String? = null,
    val category: String? = null,
    val isEquipped: Boolean = false,
    val isConsumable: Boolean = false,
    val notes: String? = null
)