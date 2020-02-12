package com.dongnh.autosoundmode.di

import androidx.room.Room
import com.dongnh.autosoundmode.db.AppDb
import org.koin.dsl.module

val databaseModule = module {
    single { Room.databaseBuilder(get(), AppDb::class.java, "log_entity").build() }
    single { get<AppDb>().logEntityDao() }
}