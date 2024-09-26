package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.data.source.local.getDatabaseBuilder
import com.ricsdev.uconnect.presentation.setupScreen.SetupViewModel
import com.ricsdev.uconnect.util.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::getDatabaseBuilder)
    singleOf(::SecureStorage)

    viewModel { SetupViewModel(get()) }

}