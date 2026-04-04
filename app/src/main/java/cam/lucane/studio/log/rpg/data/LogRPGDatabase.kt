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
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import com.google.gson.Gson
import cam.lucane.studio.log.rpg.data.dao.NoteDao
import cam.lucane.studio.log.rpg.data.entity.Note
import cam.lucane.studio.log.rpg.data.dao.StatDao
import cam.lucane.studio.log.rpg.data.entity.StatSection
import cam.lucane.studio.log.rpg.data.entity.StatWidget

@Database(
    entities = [
        Character::class,
        Ability::class,
        Item::class,
        Note::class,
        StatSection::class,
        StatWidget::class,
    ],
    version = 10,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class LogRPGDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun abilityDao(): AbilityDao
    abstract fun itemDao(): ItemDao
    abstract fun noteDao(): NoteDao
    abstract fun statDao(): StatDao

    companion object {
        @Volatile
        private var INSTANCE: LogRPGDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN credits INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE characters SET credits = (goldPieces * 100) + (silverPieces * 10) + copperPieces")
                database.execSQL("""
                    CREATE TABLE characters_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL,
                        pdfPath TEXT, currentHealth INTEGER NOT NULL, maxHealth INTEGER NOT NULL,
                        currentMana INTEGER NOT NULL, maxMana INTEGER NOT NULL,
                        currencyMode TEXT NOT NULL, credits INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    INSERT INTO characters_new
                    SELECT id, name, createdAt, updatedAt, pdfPath,
                           currentHealth, maxHealth, currentMana, maxMana, currencyMode, credits
                    FROM characters
                """)
                database.execSQL("DROP TABLE characters")
                database.execSQL("ALTER TABLE characters_new RENAME TO characters")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE items ADD COLUMN isConsumable INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN notes TEXT")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN profileImagePath TEXT")
            }
        }

        // ✨ Ajout manaMode + spellSlotsJson
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN manaMode TEXT NOT NULL DEFAULT 'MANA'")
                val defaultSlots = Gson().toJson((1..9).map { SpellSlot(it, 0, 0) })
                database.execSQL("ALTER TABLE characters ADD COLUMN spellSlotsJson TEXT NOT NULL DEFAULT '$defaultSlots'")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                characterId INTEGER NOT NULL,
                title TEXT NOT NULL DEFAULT 'Nouvelle note',
                content TEXT NOT NULL DEFAULT '',
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                FOREIGN KEY (characterId) REFERENCES characters(id) ON DELETE CASCADE
            )
        """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_characterId ON notes(characterId)")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE characters ADD COLUMN temporaryHealth INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE abilities ADD COLUMN damage TEXT")
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS stat_sections (
                id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                characterId INTEGER NOT NULL,
                title       TEXT NOT NULL,
                position    INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (characterId) REFERENCES characters(id) ON DELETE CASCADE
            )
        """)
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_stat_sections_characterId ON stat_sections(characterId)"
                )
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS stat_widgets (
                id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sectionId   INTEGER NOT NULL,
                title       TEXT NOT NULL,
                type        TEXT NOT NULL DEFAULT 'FREE',
                value       TEXT NOT NULL DEFAULT '',
                modifier    TEXT NOT NULL DEFAULT '',
                accentColor TEXT NOT NULL DEFAULT 'PURPLE',
                position    INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (sectionId) REFERENCES stat_sections(id) ON DELETE CASCADE
            )
        """)
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_stat_widgets_sectionId ON stat_widgets(sectionId)"
                )
            }
        }

        fun getDatabase(context: Context): LogRPGDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LogRPGDatabase::class.java,
                    "logrpg_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10
                    )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}