package com.camo.kripto.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camo.kripto.local.model.PriceAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPriceAlert(priceAlert: PriceAlert)

    @Query("DELETE FROM PriceAlert WHERE primaryKey =:key")
    suspend fun removePriceAlert(key: Long)

    @Query("SELECT * FROM PriceAlert")
    suspend fun getAllAlerts(): List<PriceAlert>

    @Query("SELECT COUNT() FROM PriceAlert WHERE id =:id")
    suspend fun getPriceAlertsById(id: String): Int

    @Query("DELETE FROM PriceAlert")
    suspend fun clearPriceAlerts()

    @Query("SELECT * FROM PriceAlert WHERE enabled =:enabledStatus")
    suspend fun getPriceAlertsByEnabledStatus(enabledStatus: Boolean): List<PriceAlert>

    @Query("UPDATE PriceAlert SET enabled =:newEnabled WHERE primaryKey =:primaryKey")
    suspend fun updateEnabled(primaryKey: Long, newEnabled: Boolean)

    @Query("UPDATE PriceAlert SET shown =:newShown WHERE primaryKey =:primaryKey")
    fun updateShown(primaryKey: Long, newShown: Boolean)

    @Query("SELECT * FROM PriceAlert")
    fun getAllAlertsAsFlow(): Flow<List<PriceAlert>>
}
