package com.drbrosdev.qrscannerfromlib.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.getCurrentDateTime

@Entity(tableName = "QR_CODE_TABLE")
data class QRCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long = getCurrentDateTime(),
    val data: QRCodeModel,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val codeImage: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QRCodeEntity

        if (id != other.id) return false
        if (time != other.time) return false
        if (data != other.data) return false
        if (codeImage != null) {
            if (other.codeImage == null) return false
            if (!codeImage.contentEquals(other.codeImage)) return false
        } else if (other.codeImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + time.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + (codeImage?.contentHashCode() ?: 0)
        return result
    }
}