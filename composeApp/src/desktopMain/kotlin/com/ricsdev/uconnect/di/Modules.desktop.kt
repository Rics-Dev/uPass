package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.data.source.local.DBFactory
import com.ricsdev.uconnect.presentation.setupScreen.SetupViewModel
import com.ricsdev.uconnect.util.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val platformModule = module {
//    singleOf(::DBFactory)
    single { DBFactory().createDatabase() }
    singleOf(::SecureStorage)
    viewModel { SetupViewModel(get()) }

}