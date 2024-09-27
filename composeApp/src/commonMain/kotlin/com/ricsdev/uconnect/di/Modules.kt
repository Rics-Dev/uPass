package com.ricsdev.uconnect.di

import com.ricsdev.uconnect.data.repository.RepositoryImpl
import com.ricsdev.uconnect.data.source.local.AppDatabase
import com.ricsdev.uconnect.domain.repository.Repository
import com.ricsdev.uconnect.domain.usecase.DeleteAccountUseCase
import com.ricsdev.uconnect.domain.usecase.GetAccountUseCase
import com.ricsdev.uconnect.domain.usecase.GetAllAccountsUseCase
import com.ricsdev.uconnect.domain.usecase.SaveAccountUseCase
import com.ricsdev.uconnect.presentation.account.AccountViewModel
import com.ricsdev.uconnect.presentation.home.HomeViewModel
import com.ricsdev.uconnect.presentation.home.components.modal.TwoFaSetupViewModel
import com.ricsdev.uconnect.presentation.sharedComponents.passwordGenerator.PasswordGeneratorViewModel
import com.ricsdev.uconnect.util.CryptoManager
import com.ricsdev.uconnect.util.twoFa.OtpManager
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
}