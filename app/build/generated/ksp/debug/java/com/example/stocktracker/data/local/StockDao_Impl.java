package com.example.stocktracker.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StockDao_Impl implements StockDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StockEntity> __insertionAdapterOfStockEntity;

  private final EntityDeletionOrUpdateAdapter<StockEntity> __deletionAdapterOfStockEntity;

  private final EntityDeletionOrUpdateAdapter<StockEntity> __updateAdapterOfStockEntity;

  public StockDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStockEntity = new EntityInsertionAdapter<StockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `stocks` (`id`,`name`,`symbol`,`sharesHeld`,`unitCostGbp`,`currency`,`showInWidget`,`cachedPriceGbp`,`cachedChangePercent`,`cachedPriceTimestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StockEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getSymbol());
        statement.bindDouble(4, entity.getSharesHeld());
        statement.bindDouble(5, entity.getUnitCostGbp());
        statement.bindString(6, entity.getCurrency());
        final int _tmp = entity.getShowInWidget() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getCachedPriceGbp() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getCachedPriceGbp());
        }
        if (entity.getCachedChangePercent() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getCachedChangePercent());
        }
        if (entity.getCachedPriceTimestamp() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCachedPriceTimestamp());
        }
      }
    };
    this.__deletionAdapterOfStockEntity = new EntityDeletionOrUpdateAdapter<StockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `stocks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StockEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfStockEntity = new EntityDeletionOrUpdateAdapter<StockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `stocks` SET `id` = ?,`name` = ?,`symbol` = ?,`sharesHeld` = ?,`unitCostGbp` = ?,`currency` = ?,`showInWidget` = ?,`cachedPriceGbp` = ?,`cachedChangePercent` = ?,`cachedPriceTimestamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StockEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getSymbol());
        statement.bindDouble(4, entity.getSharesHeld());
        statement.bindDouble(5, entity.getUnitCostGbp());
        statement.bindString(6, entity.getCurrency());
        final int _tmp = entity.getShowInWidget() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getCachedPriceGbp() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getCachedPriceGbp());
        }
        if (entity.getCachedChangePercent() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getCachedChangePercent());
        }
        if (entity.getCachedPriceTimestamp() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCachedPriceTimestamp());
        }
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public long insert(final StockEntity stock) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfStockEntity.insertAndReturnId(stock);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final StockEntity stock) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfStockEntity.handle(stock);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final StockEntity stock) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfStockEntity.handle(stock);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Flow<List<StockEntity>> getAllFlow() {
    final String _sql = "SELECT * FROM stocks ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stocks"}, new Callable<List<StockEntity>>() {
      @Override
      @NonNull
      public List<StockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
          final int _cursorIndexOfSharesHeld = CursorUtil.getColumnIndexOrThrow(_cursor, "sharesHeld");
          final int _cursorIndexOfUnitCostGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostGbp");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfShowInWidget = CursorUtil.getColumnIndexOrThrow(_cursor, "showInWidget");
          final int _cursorIndexOfCachedPriceGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceGbp");
          final int _cursorIndexOfCachedChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedChangePercent");
          final int _cursorIndexOfCachedPriceTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceTimestamp");
          final List<StockEntity> _result = new ArrayList<StockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpSymbol;
            _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
            final double _tmpSharesHeld;
            _tmpSharesHeld = _cursor.getDouble(_cursorIndexOfSharesHeld);
            final double _tmpUnitCostGbp;
            _tmpUnitCostGbp = _cursor.getDouble(_cursorIndexOfUnitCostGbp);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final boolean _tmpShowInWidget;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfShowInWidget);
            _tmpShowInWidget = _tmp != 0;
            final Double _tmpCachedPriceGbp;
            if (_cursor.isNull(_cursorIndexOfCachedPriceGbp)) {
              _tmpCachedPriceGbp = null;
            } else {
              _tmpCachedPriceGbp = _cursor.getDouble(_cursorIndexOfCachedPriceGbp);
            }
            final Double _tmpCachedChangePercent;
            if (_cursor.isNull(_cursorIndexOfCachedChangePercent)) {
              _tmpCachedChangePercent = null;
            } else {
              _tmpCachedChangePercent = _cursor.getDouble(_cursorIndexOfCachedChangePercent);
            }
            final Long _tmpCachedPriceTimestamp;
            if (_cursor.isNull(_cursorIndexOfCachedPriceTimestamp)) {
              _tmpCachedPriceTimestamp = null;
            } else {
              _tmpCachedPriceTimestamp = _cursor.getLong(_cursorIndexOfCachedPriceTimestamp);
            }
            _item = new StockEntity(_tmpId,_tmpName,_tmpSymbol,_tmpSharesHeld,_tmpUnitCostGbp,_tmpCurrency,_tmpShowInWidget,_tmpCachedPriceGbp,_tmpCachedChangePercent,_tmpCachedPriceTimestamp);
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
  public List<StockEntity> getAll() {
    final String _sql = "SELECT * FROM stocks ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
      final int _cursorIndexOfSharesHeld = CursorUtil.getColumnIndexOrThrow(_cursor, "sharesHeld");
      final int _cursorIndexOfUnitCostGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostGbp");
      final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
      final int _cursorIndexOfShowInWidget = CursorUtil.getColumnIndexOrThrow(_cursor, "showInWidget");
      final int _cursorIndexOfCachedPriceGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceGbp");
      final int _cursorIndexOfCachedChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedChangePercent");
      final int _cursorIndexOfCachedPriceTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceTimestamp");
      final List<StockEntity> _result = new ArrayList<StockEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final StockEntity _item;
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final String _tmpSymbol;
        _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
        final double _tmpSharesHeld;
        _tmpSharesHeld = _cursor.getDouble(_cursorIndexOfSharesHeld);
        final double _tmpUnitCostGbp;
        _tmpUnitCostGbp = _cursor.getDouble(_cursorIndexOfUnitCostGbp);
        final String _tmpCurrency;
        _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
        final boolean _tmpShowInWidget;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfShowInWidget);
        _tmpShowInWidget = _tmp != 0;
        final Double _tmpCachedPriceGbp;
        if (_cursor.isNull(_cursorIndexOfCachedPriceGbp)) {
          _tmpCachedPriceGbp = null;
        } else {
          _tmpCachedPriceGbp = _cursor.getDouble(_cursorIndexOfCachedPriceGbp);
        }
        final Double _tmpCachedChangePercent;
        if (_cursor.isNull(_cursorIndexOfCachedChangePercent)) {
          _tmpCachedChangePercent = null;
        } else {
          _tmpCachedChangePercent = _cursor.getDouble(_cursorIndexOfCachedChangePercent);
        }
        final Long _tmpCachedPriceTimestamp;
        if (_cursor.isNull(_cursorIndexOfCachedPriceTimestamp)) {
          _tmpCachedPriceTimestamp = null;
        } else {
          _tmpCachedPriceTimestamp = _cursor.getLong(_cursorIndexOfCachedPriceTimestamp);
        }
        _item = new StockEntity(_tmpId,_tmpName,_tmpSymbol,_tmpSharesHeld,_tmpUnitCostGbp,_tmpCurrency,_tmpShowInWidget,_tmpCachedPriceGbp,_tmpCachedChangePercent,_tmpCachedPriceTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<StockEntity> getWidgetStocks() {
    final String _sql = "SELECT * FROM stocks WHERE showInWidget = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
      final int _cursorIndexOfSharesHeld = CursorUtil.getColumnIndexOrThrow(_cursor, "sharesHeld");
      final int _cursorIndexOfUnitCostGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostGbp");
      final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
      final int _cursorIndexOfShowInWidget = CursorUtil.getColumnIndexOrThrow(_cursor, "showInWidget");
      final int _cursorIndexOfCachedPriceGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceGbp");
      final int _cursorIndexOfCachedChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedChangePercent");
      final int _cursorIndexOfCachedPriceTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceTimestamp");
      final List<StockEntity> _result = new ArrayList<StockEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final StockEntity _item;
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final String _tmpSymbol;
        _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
        final double _tmpSharesHeld;
        _tmpSharesHeld = _cursor.getDouble(_cursorIndexOfSharesHeld);
        final double _tmpUnitCostGbp;
        _tmpUnitCostGbp = _cursor.getDouble(_cursorIndexOfUnitCostGbp);
        final String _tmpCurrency;
        _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
        final boolean _tmpShowInWidget;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfShowInWidget);
        _tmpShowInWidget = _tmp != 0;
        final Double _tmpCachedPriceGbp;
        if (_cursor.isNull(_cursorIndexOfCachedPriceGbp)) {
          _tmpCachedPriceGbp = null;
        } else {
          _tmpCachedPriceGbp = _cursor.getDouble(_cursorIndexOfCachedPriceGbp);
        }
        final Double _tmpCachedChangePercent;
        if (_cursor.isNull(_cursorIndexOfCachedChangePercent)) {
          _tmpCachedChangePercent = null;
        } else {
          _tmpCachedChangePercent = _cursor.getDouble(_cursorIndexOfCachedChangePercent);
        }
        final Long _tmpCachedPriceTimestamp;
        if (_cursor.isNull(_cursorIndexOfCachedPriceTimestamp)) {
          _tmpCachedPriceTimestamp = null;
        } else {
          _tmpCachedPriceTimestamp = _cursor.getLong(_cursorIndexOfCachedPriceTimestamp);
        }
        _item = new StockEntity(_tmpId,_tmpName,_tmpSymbol,_tmpSharesHeld,_tmpUnitCostGbp,_tmpCurrency,_tmpShowInWidget,_tmpCachedPriceGbp,_tmpCachedChangePercent,_tmpCachedPriceTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public StockEntity getById(final long id) {
    final String _sql = "SELECT * FROM stocks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfSymbol = CursorUtil.getColumnIndexOrThrow(_cursor, "symbol");
      final int _cursorIndexOfSharesHeld = CursorUtil.getColumnIndexOrThrow(_cursor, "sharesHeld");
      final int _cursorIndexOfUnitCostGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "unitCostGbp");
      final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
      final int _cursorIndexOfShowInWidget = CursorUtil.getColumnIndexOrThrow(_cursor, "showInWidget");
      final int _cursorIndexOfCachedPriceGbp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceGbp");
      final int _cursorIndexOfCachedChangePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedChangePercent");
      final int _cursorIndexOfCachedPriceTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedPriceTimestamp");
      final StockEntity _result;
      if (_cursor.moveToFirst()) {
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final String _tmpSymbol;
        _tmpSymbol = _cursor.getString(_cursorIndexOfSymbol);
        final double _tmpSharesHeld;
        _tmpSharesHeld = _cursor.getDouble(_cursorIndexOfSharesHeld);
        final double _tmpUnitCostGbp;
        _tmpUnitCostGbp = _cursor.getDouble(_cursorIndexOfUnitCostGbp);
        final String _tmpCurrency;
        _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
        final boolean _tmpShowInWidget;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfShowInWidget);
        _tmpShowInWidget = _tmp != 0;
        final Double _tmpCachedPriceGbp;
        if (_cursor.isNull(_cursorIndexOfCachedPriceGbp)) {
          _tmpCachedPriceGbp = null;
        } else {
          _tmpCachedPriceGbp = _cursor.getDouble(_cursorIndexOfCachedPriceGbp);
        }
        final Double _tmpCachedChangePercent;
        if (_cursor.isNull(_cursorIndexOfCachedChangePercent)) {
          _tmpCachedChangePercent = null;
        } else {
          _tmpCachedChangePercent = _cursor.getDouble(_cursorIndexOfCachedChangePercent);
        }
        final Long _tmpCachedPriceTimestamp;
        if (_cursor.isNull(_cursorIndexOfCachedPriceTimestamp)) {
          _tmpCachedPriceTimestamp = null;
        } else {
          _tmpCachedPriceTimestamp = _cursor.getLong(_cursorIndexOfCachedPriceTimestamp);
        }
        _result = new StockEntity(_tmpId,_tmpName,_tmpSymbol,_tmpSharesHeld,_tmpUnitCostGbp,_tmpCurrency,_tmpShowInWidget,_tmpCachedPriceGbp,_tmpCachedChangePercent,_tmpCachedPriceTimestamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
