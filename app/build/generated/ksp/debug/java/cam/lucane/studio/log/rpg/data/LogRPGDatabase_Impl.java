package cam.lucane.studio.log.rpg.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import cam.lucane.studio.log.rpg.data.dao.AbilityDao;
import cam.lucane.studio.log.rpg.data.dao.AbilityDao_Impl;
import cam.lucane.studio.log.rpg.data.dao.CharacterDao;
import cam.lucane.studio.log.rpg.data.dao.CharacterDao_Impl;
import cam.lucane.studio.log.rpg.data.dao.ItemDao;
import cam.lucane.studio.log.rpg.data.dao.ItemDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LogRPGDatabase_Impl extends LogRPGDatabase {
  private volatile CharacterDao _characterDao;

  private volatile AbilityDao _abilityDao;

  private volatile ItemDao _itemDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `characters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `pdfPath` TEXT, `currentHealth` INTEGER NOT NULL, `maxHealth` INTEGER NOT NULL, `currentMana` INTEGER NOT NULL, `maxMana` INTEGER NOT NULL, `currencyMode` TEXT NOT NULL, `credits` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `abilities` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `characterId` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `cost` TEXT, `range` TEXT, `duration` TEXT, `category` TEXT, `notes` TEXT, FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_abilities_characterId` ON `abilities` (`characterId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `characterId` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `weight` TEXT, `category` TEXT, `isEquipped` INTEGER NOT NULL, `isConsumable` INTEGER NOT NULL, `notes` TEXT, FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_items_characterId` ON `items` (`characterId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0f8615059ca1cc5aff38abbca3abe6aa')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `characters`");
        db.execSQL("DROP TABLE IF EXISTS `abilities`");
        db.execSQL("DROP TABLE IF EXISTS `items`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCharacters = new HashMap<String, TableInfo.Column>(11);
        _columnsCharacters.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("pdfPath", new TableInfo.Column("pdfPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("currentHealth", new TableInfo.Column("currentHealth", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("maxHealth", new TableInfo.Column("maxHealth", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("currentMana", new TableInfo.Column("currentMana", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("maxMana", new TableInfo.Column("maxMana", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("currencyMode", new TableInfo.Column("currencyMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCharacters.put("credits", new TableInfo.Column("credits", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCharacters = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCharacters = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCharacters = new TableInfo("characters", _columnsCharacters, _foreignKeysCharacters, _indicesCharacters);
        final TableInfo _existingCharacters = TableInfo.read(db, "characters");
        if (!_infoCharacters.equals(_existingCharacters)) {
          return new RoomOpenHelper.ValidationResult(false, "characters(cam.lucane.studio.log.rpg.data.entity.Character).\n"
                  + " Expected:\n" + _infoCharacters + "\n"
                  + " Found:\n" + _existingCharacters);
        }
        final HashMap<String, TableInfo.Column> _columnsAbilities = new HashMap<String, TableInfo.Column>(9);
        _columnsAbilities.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("characterId", new TableInfo.Column("characterId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("cost", new TableInfo.Column("cost", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("range", new TableInfo.Column("range", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("duration", new TableInfo.Column("duration", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAbilities.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAbilities = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAbilities.add(new TableInfo.ForeignKey("characters", "CASCADE", "NO ACTION", Arrays.asList("characterId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAbilities = new HashSet<TableInfo.Index>(1);
        _indicesAbilities.add(new TableInfo.Index("index_abilities_characterId", false, Arrays.asList("characterId"), Arrays.asList("ASC")));
        final TableInfo _infoAbilities = new TableInfo("abilities", _columnsAbilities, _foreignKeysAbilities, _indicesAbilities);
        final TableInfo _existingAbilities = TableInfo.read(db, "abilities");
        if (!_infoAbilities.equals(_existingAbilities)) {
          return new RoomOpenHelper.ValidationResult(false, "abilities(cam.lucane.studio.log.rpg.data.entity.Ability).\n"
                  + " Expected:\n" + _infoAbilities + "\n"
                  + " Found:\n" + _existingAbilities);
        }
        final HashMap<String, TableInfo.Column> _columnsItems = new HashMap<String, TableInfo.Column>(10);
        _columnsItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("characterId", new TableInfo.Column("characterId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("weight", new TableInfo.Column("weight", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("isEquipped", new TableInfo.Column("isEquipped", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("isConsumable", new TableInfo.Column("isConsumable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsItems.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysItems = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysItems.add(new TableInfo.ForeignKey("characters", "CASCADE", "NO ACTION", Arrays.asList("characterId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesItems = new HashSet<TableInfo.Index>(1);
        _indicesItems.add(new TableInfo.Index("index_items_characterId", false, Arrays.asList("characterId"), Arrays.asList("ASC")));
        final TableInfo _infoItems = new TableInfo("items", _columnsItems, _foreignKeysItems, _indicesItems);
        final TableInfo _existingItems = TableInfo.read(db, "items");
        if (!_infoItems.equals(_existingItems)) {
          return new RoomOpenHelper.ValidationResult(false, "items(cam.lucane.studio.log.rpg.data.entity.Item).\n"
                  + " Expected:\n" + _infoItems + "\n"
                  + " Found:\n" + _existingItems);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0f8615059ca1cc5aff38abbca3abe6aa", "b764f6dd6e08212895cd5b5091b1fb5f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "characters","abilities","items");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `characters`");
      _db.execSQL("DELETE FROM `abilities`");
      _db.execSQL("DELETE FROM `items`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CharacterDao.class, CharacterDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AbilityDao.class, AbilityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ItemDao.class, ItemDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CharacterDao characterDao() {
    if (_characterDao != null) {
      return _characterDao;
    } else {
      synchronized(this) {
        if(_characterDao == null) {
          _characterDao = new CharacterDao_Impl(this);
        }
        return _characterDao;
      }
    }
  }

  @Override
  public AbilityDao abilityDao() {
    if (_abilityDao != null) {
      return _abilityDao;
    } else {
      synchronized(this) {
        if(_abilityDao == null) {
          _abilityDao = new AbilityDao_Impl(this);
        }
        return _abilityDao;
      }
    }
  }

  @Override
  public ItemDao itemDao() {
    if (_itemDao != null) {
      return _itemDao;
    } else {
      synchronized(this) {
        if(_itemDao == null) {
          _itemDao = new ItemDao_Impl(this);
        }
        return _itemDao;
      }
    }
  }
}
