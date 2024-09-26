package com.ricsdev.uconnect.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ricsdev.uconnect.data.model.AccountEntity
import com.ricsdev.uconnect.data.source.local.dao.AccountDao



const val DATABASE_NAME = "uconnect.db"

@Database(entities = [AccountEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase(), DB {
    abstract fun accountDao(): AccountDao
    override fun clearAllTables() {
        super.clearAllTables()
    }
}


interface DB {
    fun clearAllTables() {}
}