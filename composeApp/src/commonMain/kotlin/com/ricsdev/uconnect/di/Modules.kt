package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.presentation.homeScreen.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module



expect val platformModule: Module


val sharedModule = module {
    viewModelOf(::HomeViewModel)
}