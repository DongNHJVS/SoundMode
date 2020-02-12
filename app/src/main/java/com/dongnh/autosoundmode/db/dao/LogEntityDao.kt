package com.dongnh.autosoundmode.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dongnh.autosoundmode.db.entity.LogEntity

@Dao
interface LogEntityDao  {
    @get:Query("SELECT * FROM log_entity")
    val loadAll: List<LogEntity>

    @Insert
    fun insert(vararg users: LogEntity)

    @Query("DELETE FROM log_entity")
    fun deleteAllLog()
}