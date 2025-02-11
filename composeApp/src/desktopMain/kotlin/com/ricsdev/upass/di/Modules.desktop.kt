package com.ricsdev.upass.di

import com.ricsdev.upass.data.source.local.DBFactory
import com.ricsdev.upass.presentation.setupScreen.SetupViewModel
import com.ricsdev.upass.util.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val platformModule = module {
//    singleOf(::DBFactory)
    single { DBFactory().createDatabase() }
    singleOf(::SecureStorage)
    viewModel { SetupViewModel(get()) }

}