package com.drbrosdev.qrscannerfromlib.di

import com.drbrosdev.qrscannerfromlib.ui.codedetail.CodeDetailViewModel
import com.drbrosdev.qrscannerfromlib.ui.createcode.CreateCodeViewModel
import com.drbrosdev.qrscannerfromlib.ui.createcode.createsheet.CreateCodeBottomViewModel
import com.drbrosdev.qrscannerfromlib.ui.home.HomeViewModel
import com.drbrosdev.qrscannerfromlib.ui.localimage.LocalImageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(repo = get(), prefs = get()) }
    viewModel { CodeDetailViewModel(savedStateHandle = get(), repo = get()) }
    viewModel { CreateCodeViewModel(repo = get(), prefs = get(), savedStateHandle = get()) }
    viewModel { LocalImageViewModel(repo = get(), prefs = get()) }
    viewModel { CreateCodeBottomViewModel(repo = get(), stateHandle = get()) }
}