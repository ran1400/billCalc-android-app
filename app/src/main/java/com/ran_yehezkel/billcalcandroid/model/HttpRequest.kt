package com.ran_yehezkel.billcalcandroid.model

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.ran_yehezkel.billcalcandroid.MyApp
import com.ran_yehezkel.billcalcandroid.viewModels.Utils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.util.concurrent.TimeUnit
import org.json.JSONObject
import java.io.IOException

private const val SERVER_URL = "https://ran-y.com/bill_calc_server/"

class HttpRequest
{
    companion object
    {
        private val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        var call : Call? = null
        fun sendReceipt(
            base64Image: String,
            onSuccess: (receiptJson: String, receiptImage: ImageBitmap, uuid: String?, receiptId: String?) -> Unit,
            onNetworkFailure: () -> Unit,
            onServerFailure: () -> Unit
        )
        {

            val json = JSONObject().apply {
                put("image", base64Image)
                put("uuid", MyApp.instance.getOrCreateUuid())
            }

            val body = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(SERVER_URL + "request.php")
                .post(body)
                .build()

            val call = client.newCall(request)

            call.enqueue(object : Callback
            {
                override fun onFailure(call: Call, e: IOException)
                {
                    if (call.isCanceled())
                        return
                    onNetworkFailure()
                }

                override fun onResponse(call: Call, response: Response)
                {
                    if (call.isCanceled())
                        return

                    val responseBody = response.body?.string()
                    if (responseBody == null || !response.isSuccessful)
                        onServerFailure()
                    else
                    {
                        try {
                            val json = JSONObject(responseBody)
                            val res = if (json.has("res")) json.getString("res") else null
                            if (res == null)
                                onServerFailure()
                            else
                            {
                                val uuid = if (json.has("uuid")) json.getString("uuid") else null
                                val receiptId = if (json.has("receipt")) json.getString("receipt") else null
                                val receiptImage = Utils.base64ToImageBitmap(base64Image)
                                onSuccess(res, receiptImage, uuid, receiptId)
                            }
                        } catch (e: Exception) {
                            onServerFailure()
                        }
                    }
                }
            })
            this.call = call
        }


        fun cancelRequest()
        {
            call?.cancel()
        }

    }
}