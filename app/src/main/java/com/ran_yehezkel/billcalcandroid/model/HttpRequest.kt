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

class HttpRequest
{
    companion object
    {
        var call : Call? = null
        fun sendReceipt(
            base64Image: String,
            onSuccess: (receiptJson: String, receiptImage: ImageBitmap) -> Unit,
            onNetworkFailure: () -> Unit,
            onServerFailure: () -> Unit
        )
        {

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            val json = JSONObject().apply {
                put("image", base64Image)
                put("uuid", MyApp.instance.getOrCreateUuid())
            }

            val body = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://ran-y.com/bill_calc_server/request.php")
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
                        val res = getResFromServerResponse(responseBody)
                        if (res == null)
                            onServerFailure()
                        else
                            onSuccess(res, Utils.base64ToImageBitmap(base64Image))
                    }
                }
            })

            this.call = call
        }

        fun getResFromServerResponse(responseBody : String) : String?
        {
            try
            {
                val json = JSONObject(responseBody)
                if (json.has("res"))
                {
                    val res = json.getString("res")
                    return res
                }
                else if (json.has("error"))
                {
                    val error = json.getString("error")
                    Log.e("HomeScreenViewModel", "Error: $error")
                    return null
                }
                else
                {
                    Log.e("HomeScreenViewModel", "Error: unknown")
                    return null
                }
            }
            catch (e : Exception)
            {
                Log.e("HomeScreenViewModel", "Error: $e")
                return null
            }
        }

        fun cancelRequest()
        {
            call?.cancel()
        }

    }
}