package cam.lucane.studio.log.rpg.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import cam.lucane.studio.log.rpg.data.entity.Ability;
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
public final class AbilityDao_Impl implements AbilityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Ability> __insertionAdapterOfAbility;

  private final EntityDeletionOrUpdateAdapter<Ability> __deletionAdapterOfAbility;

  private final EntityDeletionOrUpdateAdapter<Ability> __updateAdapterOfAbility;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllAbilitiesByCharacter;

  public AbilityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAbility = new EntityInsertionAdapter<Ability>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `abilities` (`id`,`characterId`,`name`,`description`,`cost`,`range`,`duration`,`category`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Ability entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCharacterId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getDescription());
        if (entity.getCost() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCost());
        }
        if (entity.getRange() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRange());
        }
        if (entity.getDuration() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDuration());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCategory());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
      }
    };
    this.__deletionAdapterOfAbility = new EntityDeletionOrUpdateAdapter<Ability>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `abilities` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Ability entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAbility = new EntityDeletionOrUpdateAdapter<Ability>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `abilities` SET `id` = ?,`characterId` = ?,`name` = ?,`description` = ?,`cost` = ?,`range` = ?,`duration` = ?,`category` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Ability entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCharacterId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getDescription());
        if (entity.getCost() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCost());
        }
        if (entity.getRange() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRange());
        }
        if (entity.getDuration() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDuration());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCategory());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllAbilitiesByCharacter = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM abilities WHERE characterId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAbility(final Ability ability, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAbility.insertAndReturnId(ability);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAbilities(final List<Ability> abilities,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAbility.insert(abilities);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAbility(final Ability ability, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAbility.handle(ability);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAbility(final Ability ability, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAbility.handle(ability);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllAbilitiesByCharacter(final long characterId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllAbilitiesByCharacter.acquire();
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
          __preparedStmtOfDeleteAllAbilitiesByCharacter.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Ability>> getAbilitiesByCharacter(final long characterId) {
    final String _sql = "SELECT * FROM abilities WHERE characterId = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, characterId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"abilities"}, new Callable<List<Ability>>() {
      @Override
      @NonNull
      public List<Ability> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCharacterId = CursorUtil.getColumnIndexOrThrow(_cursor, "characterId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfRange = CursorUtil.getColumnIndexOrThrow(_cursor, "range");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<Ability> _result = new ArrayList<Ability>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Ability _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCharacterId;
            _tmpCharacterId = _cursor.getLong(_cursorIndexOfCharacterId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCost;
            if (_cursor.isNull(_cursorIndexOfCost)) {
              _tmpCost = null;
            } else {
              _tmpCost = _cursor.getString(_cursorIndexOfCost);
            }
            final String _tmpRange;
            if (_cursor.isNull(_cursorIndexOfRange)) {
              _tmpRange = null;
            } else {
              _tmpRange = _cursor.getString(_cursorIndexOfRange);
            }
            final String _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new Ability(_tmpId,_tmpCharacterId,_tmpName,_tmpDescription,_tmpCost,_tmpRange,_tmpDuration,_tmpCategory,_tmpNotes);
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
  public Object getAbilitiesByCharacterOnce(final long characterId,
      final Continuation<? super List<Ability>> $completion) {
    final String _sql = "SELECT * FROM abilities WHERE characterId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, characterId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Ability>>() {
      @Override
      @NonNull
      public List<Ability> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCharacterId = CursorUtil.getColumnIndexOrThrow(_cursor, "characterId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfRange = CursorUtil.getColumnIndexOrThrow(_cursor, "range");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<Ability> _result = new ArrayList<Ability>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Ability _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCharacterId;
            _tmpCharacterId = _cursor.getLong(_cursorIndexOfCharacterId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCost;
            if (_cursor.isNull(_cursorIndexOfCost)) {
              _tmpCost = null;
            } else {
              _tmpCost = _cursor.getString(_cursorIndexOfCost);
            }
            final String _tmpRange;
            if (_cursor.isNull(_cursorIndexOfRange)) {
              _tmpRange = null;
            } else {
              _tmpRange = _cursor.getString(_cursorIndexOfRange);
            }
            final String _tmpDuration;
            if (_cursor.isNull(_cursorIndexOfDuration)) {
              _tmpDuration = null;
            } else {
              _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new Ability(_tmpId,_tmpCharacterId,_tmpName,_tmpDescription,_tmpCost,_tmpRange,_tmpDuration,_tmpCategory,_tmpNotes);
            _result.add(_item);
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
