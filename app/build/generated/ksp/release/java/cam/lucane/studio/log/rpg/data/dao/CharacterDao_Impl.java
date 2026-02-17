package cam.lucane.studio.log.rpg.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import cam.lucane.studio.log.rpg.data.Converters;
import cam.lucane.studio.log.rpg.data.entity.Character;
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CharacterDao_Impl implements CharacterDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Character> __insertionAdapterOfCharacter;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Character> __deletionAdapterOfCharacter;

  private final EntityDeletionOrUpdateAdapter<Character> __updateAdapterOfCharacter;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCharacterById;

  public CharacterDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCharacter = new EntityInsertionAdapter<Character>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `characters` (`id`,`name`,`createdAt`,`updatedAt`,`pdfPath`,`currentHealth`,`maxHealth`,`currentMana`,`maxMana`,`currencyMode`,`credits`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Character entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getUpdatedAt());
        if (entity.getPdfPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPdfPath());
        }
        statement.bindLong(6, entity.getCurrentHealth());
        statement.bindLong(7, entity.getMaxHealth());
        statement.bindLong(8, entity.getCurrentMana());
        statement.bindLong(9, entity.getMaxMana());
        final String _tmp = __converters.fromCurrencyMode(entity.getCurrencyMode());
        statement.bindString(10, _tmp);
        statement.bindLong(11, entity.getCredits());
      }
    };
    this.__deletionAdapterOfCharacter = new EntityDeletionOrUpdateAdapter<Character>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `characters` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Character entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCharacter = new EntityDeletionOrUpdateAdapter<Character>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `characters` SET `id` = ?,`name` = ?,`createdAt` = ?,`updatedAt` = ?,`pdfPath` = ?,`currentHealth` = ?,`maxHealth` = ?,`currentMana` = ?,`maxMana` = ?,`currencyMode` = ?,`credits` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Character entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getUpdatedAt());
        if (entity.getPdfPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPdfPath());
        }
        statement.bindLong(6, entity.getCurrentHealth());
        statement.bindLong(7, entity.getMaxHealth());
        statement.bindLong(8, entity.getCurrentMana());
        statement.bindLong(9, entity.getMaxMana());
        final String _tmp = __converters.fromCurrencyMode(entity.getCurrencyMode());
        statement.bindString(10, _tmp);
        statement.bindLong(11, entity.getCredits());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteCharacterById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM characters WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCharacter(final Character character,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCharacter.insertAndReturnId(character);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCharacter(final Character character,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCharacter.handle(character);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCharacter(final Character character,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCharacter.handle(character);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCharacterById(final long characterId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCharacterById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, characterId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteCharacterById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Character>> getAllCharacters() {
    final String _sql = "SELECT * FROM characters ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"characters"}, new Callable<List<Character>>() {
      @Override
      @NonNull
      public List<Character> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfCurrentHealth = CursorUtil.getColumnIndexOrThrow(_cursor, "currentHealth");
          final int _cursorIndexOfMaxHealth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHealth");
          final int _cursorIndexOfCurrentMana = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMana");
          final int _cursorIndexOfMaxMana = CursorUtil.getColumnIndexOrThrow(_cursor, "maxMana");
          final int _cursorIndexOfCurrencyMode = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyMode");
          final int _cursorIndexOfCredits = CursorUtil.getColumnIndexOrThrow(_cursor, "credits");
          final List<Character> _result = new ArrayList<Character>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Character _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final int _tmpCurrentHealth;
            _tmpCurrentHealth = _cursor.getInt(_cursorIndexOfCurrentHealth);
            final int _tmpMaxHealth;
            _tmpMaxHealth = _cursor.getInt(_cursorIndexOfMaxHealth);
            final int _tmpCurrentMana;
            _tmpCurrentMana = _cursor.getInt(_cursorIndexOfCurrentMana);
            final int _tmpMaxMana;
            _tmpMaxMana = _cursor.getInt(_cursorIndexOfMaxMana);
            final CurrencyMode _tmpCurrencyMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCurrencyMode);
            _tmpCurrencyMode = __converters.toCurrencyMode(_tmp);
            final int _tmpCredits;
            _tmpCredits = _cursor.getInt(_cursorIndexOfCredits);
            _item = new Character(_tmpId,_tmpName,_tmpCreatedAt,_tmpUpdatedAt,_tmpPdfPath,_tmpCurrentHealth,_tmpMaxHealth,_tmpCurrentMana,_tmpMaxMana,_tmpCurrencyMode,_tmpCredits);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Character> getCharacterById(final long characterId) {
    final String _sql = "SELECT * FROM characters WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, characterId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"characters"}, new Callable<Character>() {
      @Override
      @Nullable
      public Character call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfCurrentHealth = CursorUtil.getColumnIndexOrThrow(_cursor, "currentHealth");
          final int _cursorIndexOfMaxHealth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHealth");
          final int _cursorIndexOfCurrentMana = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMana");
          final int _cursorIndexOfMaxMana = CursorUtil.getColumnIndexOrThrow(_cursor, "maxMana");
          final int _cursorIndexOfCurrencyMode = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyMode");
          final int _cursorIndexOfCredits = CursorUtil.getColumnIndexOrThrow(_cursor, "credits");
          final Character _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final int _tmpCurrentHealth;
            _tmpCurrentHealth = _cursor.getInt(_cursorIndexOfCurrentHealth);
            final int _tmpMaxHealth;
            _tmpMaxHealth = _cursor.getInt(_cursorIndexOfMaxHealth);
            final int _tmpCurrentMana;
            _tmpCurrentMana = _cursor.getInt(_cursorIndexOfCurrentMana);
            final int _tmpMaxMana;
            _tmpMaxMana = _cursor.getInt(_cursorIndexOfMaxMana);
            final CurrencyMode _tmpCurrencyMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCurrencyMode);
            _tmpCurrencyMode = __converters.toCurrencyMode(_tmp);
            final int _tmpCredits;
            _tmpCredits = _cursor.getInt(_cursorIndexOfCredits);
            _result = new Character(_tmpId,_tmpName,_tmpCreatedAt,_tmpUpdatedAt,_tmpPdfPath,_tmpCurrentHealth,_tmpMaxHealth,_tmpCurrentMana,_tmpMaxMana,_tmpCurrencyMode,_tmpCredits);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCharacterByIdOnce(final long characterId,
      final Continuation<? super Character> $completion) {
    final String _sql = "SELECT * FROM characters WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, characterId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Character>() {
      @Override
      @Nullable
      public Character call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfCurrentHealth = CursorUtil.getColumnIndexOrThrow(_cursor, "currentHealth");
          final int _cursorIndexOfMaxHealth = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHealth");
          final int _cursorIndexOfCurrentMana = CursorUtil.getColumnIndexOrThrow(_cursor, "currentMana");
          final int _cursorIndexOfMaxMana = CursorUtil.getColumnIndexOrThrow(_cursor, "maxMana");
          final int _cursorIndexOfCurrencyMode = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyMode");
          final int _cursorIndexOfCredits = CursorUtil.getColumnIndexOrThrow(_cursor, "credits");
          final Character _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final int _tmpCurrentHealth;
            _tmpCurrentHealth = _cursor.getInt(_cursorIndexOfCurrentHealth);
            final int _tmpMaxHealth;
            _tmpMaxHealth = _cursor.getInt(_cursorIndexOfMaxHealth);
            final int _tmpCurrentMana;
            _tmpCurrentMana = _cursor.getInt(_cursorIndexOfCurrentMana);
            final int _tmpMaxMana;
            _tmpMaxMana = _cursor.getInt(_cursorIndexOfMaxMana);
            final CurrencyMode _tmpCurrencyMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCurrencyMode);
            _tmpCurrencyMode = __converters.toCurrencyMode(_tmp);
            final int _tmpCredits;
            _tmpCredits = _cursor.getInt(_cursorIndexOfCredits);
            _result = new Character(_tmpId,_tmpName,_tmpCreatedAt,_tmpUpdatedAt,_tmpPdfPath,_tmpCurrentHealth,_tmpMaxHealth,_tmpCurrentMana,_tmpMaxMana,_tmpCurrencyMode,_tmpCredits);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
