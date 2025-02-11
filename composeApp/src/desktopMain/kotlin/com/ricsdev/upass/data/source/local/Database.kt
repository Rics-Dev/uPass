package com.ricsdev.upass.data.source.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.util.Locale

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
//    val dbFile = File(System.getProperty("java.io.tmpdir"), "uconnect.db")
    val dbFile = when {
        System.getProperty("os.name").lowercase(Locale.ROOT).contains("linux") -> {
            val homeDir = System.getProperty("user.home")
            File(homeDir, ".local/share/uconnect/.uconnectdb/uconnect.db")
        }

        else -> {
            // Fallback for other operating systems
            File(System.getProperty("user.home"), ".uconnect/uconnect.db")
        }
    }

    dbFile.parentFile?.mkdirs()

    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
