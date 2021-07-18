package com.drbrosdev.qrscannerfromlib.di

import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import org.koin.dsl.module

val repoModule = module {
    single { CodeRepository(db = get()) }
}