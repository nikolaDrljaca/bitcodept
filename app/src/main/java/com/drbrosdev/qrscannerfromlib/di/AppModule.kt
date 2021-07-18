package com.drbrosdev.qrscannerfromlib.di

import android.content.Context
import androidx.room.Room
import com.drbrosdev.qrscannerfromlib.database.CodeDatabase
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.datastore.datastore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun provideDatabase(context: Context) =
    Room.databaseBuilder(
        context,
        CodeDatabase::class.java,
        "qr_code_database"
    ).build()

fun provideDatastore(context: Context) = AppPreferences(context.datastore)

val appModule = module {
    single { provideDatabase(androidContext()) }
    factory { provideDatastore(androidContext()) }
}