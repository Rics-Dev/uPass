package com.ricsdev.uconnect.data.source.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.Locale


actual class DBFactory {
    actual fun createDatabase(): AppDatabase {
        val dbFile = when {
            System.getProperty("os.name").lowercase(Locale.ROOT).contains("linux") -> {
                val homeDir = System.getProperty("user.home")
                File(homeDir, ".local/share/uconnect/.uconnectdb/uconnect.db")
            }
            else -> {
                File(System.getProperty("user.home"), ".uconnect/uconnect.db")
            }
        }
        dbFile.parentFile.mkdirs() // Ensure the directory exists
        return Room.databaseBuilder<AppDatabase>(dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

































