package com.drbrosdev.qrscannerfromlib.network

import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resumeWithException

fun Call.asFlow(toThrow: Boolean = false) = callbackFlow {
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            if (toThrow) cancel("Api error", e)
            else trySend(null)
        }

        override fun onResponse(call: Call, response: Response) {
            trySend(response.body()?.byteStream()?.readBytes())
        }

    })

    awaitClose {  }
}

/*
For single-shot uses, such as a network request.
 */
suspend fun Call.await(): ByteArray? = suspendCancellableCoroutine { continuation ->
    val callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            continuation.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            continuation.resume(response.body()?.byteStream()?.readBytes(), null)
        }
    }
    enqueue(callback)
}