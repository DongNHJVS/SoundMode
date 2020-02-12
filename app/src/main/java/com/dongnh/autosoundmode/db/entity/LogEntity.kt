package com.dongnh.autosoundmode.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_entity")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    var date: String? = "",
    var time: String? = "",
    var mode: String? = ""
)