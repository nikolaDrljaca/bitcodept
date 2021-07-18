package com.drbrosdev.qrscannerfromlib.di

import com.drbrosdev.qrscannerfromlib.ui.codedetail.CodeDetailViewModel
import com.drbrosdev.qrscannerfromlib.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(repo = get(), prefs = get()) }
    viewModel { CodeDetailViewModel(savedStateHandle = get(), repo = get()) }
}