package com.drbrosdev.qrscannerfromlib.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE QR_CODE_TABLE ADD COLUMN userCreated INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_3_4 = object : Migration(3,4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE QR_CODE_TABLE ADD COLUMN desc TEXT DEFAULT '' NOT NULL")
    }
}