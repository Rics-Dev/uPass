package com.ricsdev.upass.data.source.local

expect class DBFactory {
    fun createDatabase(): AppDatabase
}