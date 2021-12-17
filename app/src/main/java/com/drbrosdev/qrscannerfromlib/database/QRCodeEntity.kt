package com.drbrosdev.qrscannerfromlib.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.getCurrentDateTime

@Entity(tableName = "QR_CODE_TABLE")
data class QRCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long = getCurrentDateTime(),
    val data: QRCodeModel,
    val userCreated: Int = 0
)