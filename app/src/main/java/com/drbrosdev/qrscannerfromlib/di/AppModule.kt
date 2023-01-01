package com.drbrosdev.qrscannerfromlib.di

import android.content.Context
import androidx.room.Room
import com.drbrosdev.qrscannerfromlib.billing.BillingClientWrapper
import com.drbrosdev.qrscannerfromlib.database.CodeDatabase
import com.drbrosdev.qrscannerfromlib.database.MIGRATION_1_2
import com.drbrosdev.qrscannerfromlib.database.MIGRATION_3_4
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.datastore.datastore
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private fun provideDatabase(context: Context) =
    Room.databaseBuilder(
        context,
        CodeDatabase::class.java,
        "qr_code_database"
    )
        .addMigrations(MIGRATION_1_2)
        .addMigrations(MIGRATION_3_4)
        .build()

private fun provideDatastore(context: Context) = AppPreferences(context.datastore)

private fun provideQrScannerOptions() = BarcodeScannerOptions.Builder()
    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
    .build()

private fun provideQrCodeScanner(options: BarcodeScannerOptions) =
    BarcodeScanning.getClient(options)

private fun provideBillingWrapper(context: Context): BillingClientWrapper =
    BillingClientWrapper(context)

private fun provideReviewManager(context: Context) : ReviewManager =
    ReviewManagerFactory.create(context)

val appModule = module {
    single { provideDatabase(androidContext()) }
    factory { provideDatastore(androidContext()) }
    factory { provideQrScannerOptions() }
    single { provideQrCodeScanner(get()) }
    single { provideBillingWrapper(androidContext()) }
    single { provideReviewManager(androidContext()) }
}