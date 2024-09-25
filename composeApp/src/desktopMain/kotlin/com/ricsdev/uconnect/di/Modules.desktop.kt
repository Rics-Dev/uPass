package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.data.source.local.getDatabaseBuilder
import com.ricsdev.uconnect.util.BiometricAuth
import com.ricsdev.uconnect.util.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::getDatabaseBuilder)
    singleOf(::SecureStorage)
    singleOf(::BiometricAuth)

}