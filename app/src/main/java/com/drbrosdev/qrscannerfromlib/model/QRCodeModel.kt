package com.drbrosdev.qrscannerfromlib.model

sealed class QRCodeModel {
    data class UrlModel(
        val rawValue: String,
        val title: String,
        val link: String
    ) : QRCodeModel() {
        override fun toString() = "url$DELIMITER$title$DELIMITER$link$DELIMITER$rawValue"
    }

    data class PlainModel(
        val rawValue: String
    ) : QRCodeModel() {
        override fun toString() = "plain$DELIMITER$rawValue"
    }

    data class SmsModel(
        val rawValue: String,
        val message: String,
        val phoneNumber: String
    ) : QRCodeModel() {
        override fun toString() = "sms$DELIMITER$message$DELIMITER$phoneNumber$DELIMITER$rawValue"
    }

    data class GeoPointModel(
        val rawValue: String,
        val lat: Double,
        val lng: Double
    ) : QRCodeModel() {
        override fun toString() = "geo$DELIMITER$lat$lng$DELIMITER$rawValue"
    }

    data class EmailModel(
        val rawValue: String,
        val address: String,
        val body: String,
        val subject: String,
    ) : QRCodeModel() {
        override fun toString() =
            "email$DELIMITER$address$DELIMITER$body$DELIMITER$subject$DELIMITER$rawValue"
    }

    data class PhoneModel(
        val rawValue: String,
        val number: String
    ) : QRCodeModel() {
        override fun toString() = "phone$DELIMITER$number$DELIMITER$rawValue"
    }

    data class ContactInfoModel(
        val rawValue: String,
        val name: String,
        val email: String,
        val phone: String
    ) : QRCodeModel() {
        override fun toString() =
            "contact$DELIMITER$name$DELIMITER$email$DELIMITER$phone$DELIMITER$rawValue"
    }

    companion object {
        const val DELIMITER = "^^--^^"
    }
}