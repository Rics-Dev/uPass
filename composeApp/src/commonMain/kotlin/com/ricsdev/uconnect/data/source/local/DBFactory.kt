package com.ricsdev.uconnect.data.source.local

expect class DBFactory {
    fun createDatabase(): AppDatabase
}