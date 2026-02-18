package cam.lucane.studio.log.rpg.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cam.lucane.studio.log.rpg.data.dao.AbilityDao
import cam.lucane.studio.log.rpg.data.dao.CharacterDao
import cam.lucane.studio.log.rpg.data.dao.ItemDao
import cam.lucane.studio.log.rpg.data.entity.Ability
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.Item

@Database(
    entities = [Character::class, Ability::class, Item::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LogRPGDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun abilityDao(): AbilityDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: LogRPGDatabase? = null

        // Migration 1 → 2 (déjà existante)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN credits INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE characters SET credits = (goldPieces * 100) + (silverPieces * 10) + copperPieces")

                database.execSQL("""
                    CREATE TABLE characters_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        pdfPath TEXT,
                        currentHealth INTEGER NOT NULL,
                        maxHealth INTEGER NOT NULL,
                        currentMana INTEGER NOT NULL,
                        maxMana INTEGER NOT NULL,
                        currencyMode TEXT NOT NULL,
                        credits INTEGER NOT NULL
                    )
                """)

                database.execSQL("""
                    INSERT INTO characters_new 
                    SELECT id, name, createdAt, updatedAt, pdfPath, 
                           currentHealth, maxHealth, currentMana, maxMana, 
                           currencyMode, credits
                    FROM characters
                """)

                database.execSQL("DROP TABLE characters")
                database.execSQL("ALTER TABLE characters_new RENAME TO characters")
            }
        }

        // ✅ NOUVELLE Migration 2 → 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE items ADD COLUMN isConsumable INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN notes TEXT")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN profileImagePath TEXT")
            }
        }


        fun getDatabase(context: Context): LogRPGDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogRPGDatabase::class.java,
                    "logrpg_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)  // ✅ Ajouter
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}