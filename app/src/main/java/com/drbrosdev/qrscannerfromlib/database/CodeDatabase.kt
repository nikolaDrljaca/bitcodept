package com.drbrosdev.qrscannerfromlib.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [QRCodeEntity::class],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 2,
            to = 3,
            spec = CodeDatabase.DeleteCodeImageColumnMigration::class
        )
    ]
)
@TypeConverters(QRModelConverter::class)
abstract class CodeDatabase: RoomDatabase() {
    abstract fun codeDao(): QRCodeDao

    @DeleteColumn(tableName = "QR_CODE_TABLE", columnName = "codeImage")
    class DeleteCodeImageColumnMigration: AutoMigrationSpec
}