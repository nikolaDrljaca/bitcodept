package com.drbrosdev.qrscannerfromlib.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [QRCodeEntity::class], version = 1, exportSchema = false)
@TypeConverters(QRModelConverter::class)
abstract class CodeDatabase: RoomDatabase() {
    abstract fun codeDao(): QRCodeDao
}