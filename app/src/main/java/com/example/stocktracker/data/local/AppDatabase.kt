package com.example.stocktracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [StockEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "stock_db")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL(
                                "INSERT INTO stocks (name, symbol, sharesHeld, unitCostGbp, currency, showInWidget) " +
                                "VALUES ('Alphabet (Google)', 'GOOG', 50.0, 1.0, 'USD', 0)"
                            )
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
