package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.util.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::SecureStorage)
}