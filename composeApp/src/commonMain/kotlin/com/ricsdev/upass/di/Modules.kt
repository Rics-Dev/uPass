package com.ricsdev.upass.di

import com.ricsdev.upass.data.repository.RepositoryImpl
import com.ricsdev.upass.data.source.local.AppDatabase
import com.ricsdev.upass.domain.repository.Repository
import com.ricsdev.upass.domain.usecase.DeleteAccountUseCase
import com.ricsdev.upass.domain.usecase.GetAccountUseCase
import com.ricsdev.upass.domain.usecase.GetAllAccountsUseCase
import com.ricsdev.upass.domain.usecase.SaveAccountUseCase
import com.ricsdev.upass.presentation.account.AccountViewModel
import com.ricsdev.upass.presentation.home.AuthViewModel
import com.ricsdev.upass.presentation.home.HomeViewModel
import com.ricsdev.upass.presentation.home.components.modal.TwoFaSetupViewModel
import com.ricsdev.upass.presentation.sharedComponents.passwordGenerator.PasswordGeneratorViewModel
import com.ricsdev.upass.util.CryptoManager
import com.ricsdev.upass.util.twoFa.OtpManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


expect val platformModule: Module


val sharedModule = module {
    single { get<AppDatabase>().accountDao() }

    // Repository
    singleOf(::RepositoryImpl).bind<Repository>()

    // Use Cases
    singleOf(::GetAllAccountsUseCase)
    singleOf(::GetAccountUseCase)
    singleOf(::SaveAccountUseCase)
    singleOf(::DeleteAccountUseCase)

    // Utilities
    singleOf(::CryptoManager)
    singleOf(::OtpManager)


    // ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::PasswordGeneratorViewModel)
    viewModelOf(::TwoFaSetupViewModel)
    viewModelOf(::AuthViewModel)
}