package com.example.inspectionlab4

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val BASE_URL = "http://10.0.2.2:8765"

private val client = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

suspend fun loadInspectionTasks(
    room: String,
    scenario: DemoScenario,
    requestKey: String
): List<InspectionTask> = withContext(Dispatchers.IO) {
    val url = "$BASE_URL/api/tasks".toHttpUrl().newBuilder()
        .addQueryParameter("room", room)
        .addQueryParameter("scenario", scenario.value)
        .addQueryParameter("key", requestKey)
        .build()

    val body = awaitBody(
        Request.Builder()
            .url(url)
            .build()
    )

    val root = JSONObject(body)
    check(root.getString("room") == room) {
        "响应实验室与请求不一致"
    }

    val array = root.getJSONArray("tasks")

    return@withContext buildList {
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)

            add(
                InspectionTask(
                    id = item.getString("id"),
                    deviceName = item.getString("deviceName"),
                    location = item.getString("location"),
                    status = item.getString("status")
                )
            )
        }
    }
}

private suspend fun awaitBody(request: Request): String =
    suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)

        continuation.invokeOnCancellation {
            call.cancel()
            Log.i("InspectionLab", "HTTP_CANCEL ${request.url}")
        }

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.use {
                        if (!it.isSuccessful) {
                            throw IOException("HTTP ${it.code}")
                        }

                        it.body?.string()
                            ?: throw IOException("响应正文为空")
                    }

                    if (continuation.isActive) {
                        continuation.resume(body)
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }
            }
        })
    }