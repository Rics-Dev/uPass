package com.ricsdev.upass.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

actual class DBFactory(private val context: Context) {
    actual fun createDatabase(): AppDatabase {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(context, dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}