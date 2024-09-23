package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.presentation.account.AccountViewModel
import com.ricsdev.uconnect.presentation.home.HomeViewModel
import com.ricsdev.uconnect.presentation.sharedComponents.passwordGenerator.PasswordGeneratorViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module



expect val platformModule: Module


val sharedModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::PasswordGeneratorViewModel)

}