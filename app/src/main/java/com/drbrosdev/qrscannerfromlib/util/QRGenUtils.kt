package com.drbrosdev.qrscannerfromlib.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.glxn.qrgen.android.QRCode

object QRGenUtils {
    fun createCodeBitmap(codeContent: String, colorInt: Int): Bitmap {
        return try {
            val byteArray by lazy {
                QRCode
                    .from(codeContent)
                    .withSize(250, 250)
                    .withColor(0xFF000000.toInt(), colorInt)
                    .stream()
                    .toByteArray()
            }
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888)
        }
    }
}

