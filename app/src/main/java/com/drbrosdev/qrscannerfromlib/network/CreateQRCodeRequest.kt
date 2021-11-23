package com.drbrosdev.qrscannerfromlib.network

import kotlinx.coroutines.flow.Flow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

class CreateQRCodeRequest {
    private val client = OkHttpClient()
    /*
    Creates a call with the codeContent(rawData) and a colorInt to specify code background(whitespace)
    This colorInt matches the code background so they blend. Gives off a lambda that passes
    the bytearray of the image
     */
    fun createCall(
        codeContent: String,
        colorInt: Int,
        onImageLoaded: (ByteArray?) -> Unit,
        onFail: (String?) -> Unit = {}
    ) {
        val hex = Integer.toHexString(colorInt).substring(2)
        val encoded = URLEncoder.encode(codeContent, "utf-8")
        val request = Request.Builder()
            .url("http://api.qrserver.com/v1/create-qr-code/?data=$encoded&size=250x250&bgcolor=$hex")
            .build()
        
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFail(e.localizedMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val stream = response.body()?.byteStream()?.readBytes()
                    onImageLoaded(stream)
                } catch (e: Exception) {
                    onFail(e.localizedMessage)
                }
            }
        })
    }

    fun createCall(
        codeContent: String,
        colorInt: Int
    ): Flow<ByteArray?> {
        val hex = Integer.toHexString(colorInt).substring(2)
        val encoded = URLEncoder.encode(codeContent, "utf-8")
        val request = Request.Builder()
            .url("http://api.qrserver.com/v1/create-qr-code/?data=$encoded&size=250x250&bgcolor=$hex")
            .build()

        return client.newCall(request).asFlow()
    }
}
