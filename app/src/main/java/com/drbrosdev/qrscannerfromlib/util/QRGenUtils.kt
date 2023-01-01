package com.drbrosdev.qrscannerfromlib.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.scheme.VCard

object QRGenUtils {
    fun createCodeBitmap(codeContent: String, colorInt: Int, width: Int = 250, height: Int = 250): Bitmap {
        return try {
            val byteArray by lazy {
                QRCode
                    .from(codeContent)
                    .withSize(width, height)
                    .withColor(0xFF000000.toInt(), colorInt)
                    .stream()
                    .toByteArray()
            }
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888)
        }
    }

    fun generateContact(
        name: String,
        phoneNumber: String,
        email: String,
        website: String = "",
        address: String = ""
    ): String {
        return VCard().also {
            it.name = name
            it.website = website
            it.phoneNumber = phoneNumber
            it.email = email
            it.address = address
        }.generateString()
    }

    fun generateRawSms(phoneNumber: String, message: String): String {
        return buildString {
            append("smsto:")
            append(phoneNumber).append(":")
            append(message)
        }
    }

    fun generateRawEmail(email: String, subject: String = "", body: String = ""): String {
        return buildString {
            append("MATMSG:TO:")
            append(email).append(";")
            append("SUB:")
            append(subject).append(";")
            append("BODY:")
            append(body).append(";;")
        }
    }

    fun generateRawWifi(ssid: String, password: String): String {
        return buildString {
            append("WIFI:T:WPA;")
            append("S:").append(ssid).append(";")
            append("P:").append(password).append(";")
        }
    }

    fun generateRawUrl(url: String): String {
        val trimmed = url.trim()
        return buildString {
            if(trimmed.startsWith("http://", true) or trimmed.startsWith("https://", true)) {
                append(trimmed)
            } else {
                append("http://")
                append(trimmed)
            }
        }
    }
}

