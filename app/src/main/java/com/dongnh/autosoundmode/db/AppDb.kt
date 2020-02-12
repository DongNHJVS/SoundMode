package com.dongnh.autosoundmode.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dongnh.autosoundmode.db.dao.LogEntityDao
import com.dongnh.autosoundmode.db.entity.LogEntity

@Database(entities = [LogEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun logEntityDao(): LogEntityDao
}