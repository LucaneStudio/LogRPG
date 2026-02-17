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
import cam.lucane.studio.log.rpg.data.entity.Item;
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
public final class ItemDao_Impl implements ItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Item> __insertionAdapterOfItem;

  private final EntityDeletionOrUpdateAdapter<Item> __deletionAdapterOfItem;

  private final EntityDeletionOrUpdateAdapter<Item> __updateAdapterOfItem;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllItemsByCharacter;

  public ItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfItem = new EntityInsertionAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `items` (`id`,`characterId`,`name`,`description`,`quantity`,`weight`,`category`,`isEquipped`,`isConsumable`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCharacterId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getDescription());
        statement.bindLong(5, entity.getQuantity());
        if (entity.getWeight() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getWeight());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCategory());
        }
        final int _tmp = entity.isEquipped() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isConsumable() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getNotes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNotes());
        }
      }
    };
    this.__deletionAdapterOfItem = new EntityDeletionOrUpdateAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfItem = new EntityDeletionOrUpdateAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `items` SET `id` = ?,`characterId` = ?,`name` = ?,`description` = ?,`quantity` = ?,`weight` = ?,`category` = ?,`isEquipped` = ?,`isConsumable` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCharacterId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getDescription());
        statement.bindLong(5, entity.getQuantity());
        if (entity.getWeight() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getWeight());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCategory());
        }
        final int _tmp = entity.isEquipped() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isConsumable() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getNotes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNotes());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllItemsByCharacter = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM items WHERE characterId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertItem(final Item item, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfItem.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<Item> items, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfItem.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItem(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateItem(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllItemsByCharacter(final long characterId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllItemsByCharacter.acquire();
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
          __preparedStmtOfDeleteAllItemsByCharacter.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Item>> getItemsByCharacter(final long characterId) {
    final String _sql = "SELECT * FROM items WHERE characterId = ? ORDER BY category ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, characterId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCharacterId = CursorUtil.getColumnIndexOrThrow(_cursor, "characterId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsEquipped = CursorUtil.getColumnIndexOrThrow(_cursor, "isEquipped");
          final int _cursorIndexOfIsConsumable = CursorUtil.getColumnIndexOrThrow(_cursor, "isConsumable");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCharacterId;
            _tmpCharacterId = _cursor.getLong(_cursorIndexOfCharacterId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getString(_cursorIndexOfWeight);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsEquipped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEquipped);
            _tmpIsEquipped = _tmp != 0;
            final boolean _tmpIsConsumable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsConsumable);
            _tmpIsConsumable = _tmp_1 != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new Item(_tmpId,_tmpCharacterId,_tmpName,_tmpDescription,_tmpQuantity,_tmpWeight,_tmpCategory,_tmpIsEquipped,_tmpIsConsumable,_tmpNotes);
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
  public Object getItemsByCharacterOnce(final long characterId,
      final Continuation<? super List<Item>> $completion) {
    final String _sql = "SELECT * FROM items WHERE characterId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, characterId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCharacterId = CursorUtil.getColumnIndexOrThrow(_cursor, "characterId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "weight");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsEquipped = CursorUtil.getColumnIndexOrThrow(_cursor, "isEquipped");
          final int _cursorIndexOfIsConsumable = CursorUtil.getColumnIndexOrThrow(_cursor, "isConsumable");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCharacterId;
            _tmpCharacterId = _cursor.getLong(_cursorIndexOfCharacterId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpWeight;
            if (_cursor.isNull(_cursorIndexOfWeight)) {
              _tmpWeight = null;
            } else {
              _tmpWeight = _cursor.getString(_cursorIndexOfWeight);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final boolean _tmpIsEquipped;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEquipped);
            _tmpIsEquipped = _tmp != 0;
            final boolean _tmpIsConsumable;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsConsumable);
            _tmpIsConsumable = _tmp_1 != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new Item(_tmpId,_tmpCharacterId,_tmpName,_tmpDescription,_tmpQuantity,_tmpWeight,_tmpCategory,_tmpIsEquipped,_tmpIsConsumable,_tmpNotes);
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
