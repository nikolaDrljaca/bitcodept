package com.drbrosdev.qrscannerfromlib.network

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

fun Call.asFlow() = callbackFlow {
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            trySend(null)
        }

        override fun onResponse(call: Call, response: Response) {
            trySend(response.body()?.byteStream()?.readBytes())
        }

    })

    awaitClose {  }
}
