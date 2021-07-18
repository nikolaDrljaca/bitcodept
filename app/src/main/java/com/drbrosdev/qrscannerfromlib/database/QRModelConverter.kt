package com.drbrosdev.qrscannerfromlib.database

import androidx.room.TypeConverter
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel

class QRModelConverter {

    @TypeConverter
    fun toModel(value: String): QRCodeModel {
        val split = value.split(QRCodeModel.DELIMITER)
        return when (split[0]) {
            "plain" -> {
                QRCodeModel.PlainModel(split[1])
            }
            "url" -> {
                QRCodeModel.UrlModel(title = split[1], link = split[2], rawValue = split[3])
            }
            "sms" -> {
                QRCodeModel.SmsModel(message = split[1], phoneNumber = split[2], rawValue = split[3])
            }
            "geo" -> {
                QRCodeModel.GeoPointModel(lat = split[1].toDouble(), lng = split[2].toDouble(), rawValue = split[3])
            }
            "email" -> {
                QRCodeModel.EmailModel(address = split[1], body = split[2], subject = split[3], rawValue = split[4])
            }
            "phone" -> {
                QRCodeModel.PhoneModel(number = split[1], rawValue = split[2])
            }
            "contact" -> {
                QRCodeModel.ContactInfoModel(name = split[1], email = split[2], phone = split[3], rawValue = split[4])
            }
            else -> { QRCodeModel.PlainModel("") }
        }
    }

    @TypeConverter
    fun fromModel(model: QRCodeModel): String {
        return when(model) {
            is QRCodeModel.PlainModel -> { model.toString() }
            is QRCodeModel.SmsModel -> { model.toString() }
            is QRCodeModel.UrlModel -> { model.toString() }
            is QRCodeModel.GeoPointModel -> { model.toString() }
            is QRCodeModel.ContactInfoModel -> { model.toString() }
            is QRCodeModel.EmailModel -> { model.toString() }
            is QRCodeModel.PhoneModel -> { model.toString() }
        }
    }
}