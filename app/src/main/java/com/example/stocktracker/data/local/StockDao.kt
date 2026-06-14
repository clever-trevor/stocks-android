package com.example.stocktracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM stocks ORDER BY name ASC")
    fun getAllFlow(): Flow<List<StockEntity>>

    @Query("SELECT * FROM stocks ORDER BY name ASC")
    fun getAll(): List<StockEntity>

    @Query("SELECT * FROM stocks WHERE showInWidget = 1 ORDER BY name ASC")
    fun getWidgetStocks(): List<StockEntity>

    @Query("SELECT * FROM stocks WHERE id = :id")
    fun getById(id: Long): StockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stock: StockEntity): Long

    @Update
    fun update(stock: StockEntity)

    @Delete
    fun delete(stock: StockEntity)
}
