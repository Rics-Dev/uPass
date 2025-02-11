package com.ricsdev.upass.di

import com.ricsdev.upass.data.source.local.DBFactory
import com.ricsdev.upass.presentation.setupScreen.SetupViewModel
import com.ricsdev.upass.util.SecureStorage
import dev.icerock.moko.biometry.BiometryAuthenticator
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    single { DBFactory(get()).createDatabase() }
//    singleOf(::DBFactory)


    singleOf(::SecureStorage)
    singleOf(::BiometryAuthenticator)
    viewModel { SetupViewModel(get(), get<BiometryAuthenticator>()) }
}