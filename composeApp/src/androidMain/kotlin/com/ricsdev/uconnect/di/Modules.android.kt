package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.data.source.local.getDatabaseBuilder
import com.ricsdev.uconnect.presentation.setupScreen.SetupViewModel
import com.ricsdev.uconnect.util.SecureStorage
import dev.icerock.moko.biometry.BiometryAuthenticator
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module



actual val platformModule = module {
    singleOf(::getDatabaseBuilder)
    singleOf(::SecureStorage)
    singleOf(::BiometryAuthenticator)
    viewModel { SetupViewModel(get(), get<BiometryAuthenticator>()) }
}