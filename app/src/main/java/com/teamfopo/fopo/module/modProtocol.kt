package com.teamfopo.fopo.module

import android.util.Log
import okhttp3.*
import java.io.InputStream

class modProtocol{
    var blFinish: Boolean = false

    fun getResultString(strURL: String, argType: Array<String>, argValues: Array<String>): String{
        blFinish = false
        val frmBody = FormBody.Builder()
        if (argType.isNotEmpty()){
            Log.d("modProtocol","[String] 배열이 존재합니다.")
            for (i in 0 until argType.size) {
                frmBody.add(argType[i], argValues[i])
                Log.d("modProtocol","[String] 배열 추가 : "+argType[i]+" / "+argValues[i])
            }
        }

        val requestBody: RequestBody = frmBody.build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(strURL)
            .post(requestBody)
            .build()

        var response: Response = client.newCall(request).execute()

        response = client.newCall(request).execute()
        var tmpResult = response.body()?.string()!!
        blFinish = true

        return tmpResult
    }

    fun getResultByteArray(strURL: String, argType: Array<String>, argValues: Array<String>): ByteArray {
        blFinish = false
        val frmBody = FormBody.Builder()
        if (argType.isNotEmpty()){
            Log.d("modProtocol","[ByteArray] 배열이 존재합니다.")
            for (i in 0 until argType.size) {
                frmBody.add(argType[i], argValues[i])
                Log.d("modProtocol","[ByteArray] 배열 추가 : "+argType[i]+" / "+argValues[i])
            }
        }

        val requestBody: RequestBody = frmBody.build() as RequestBody

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(strURL)
            .post(requestBody)
            .build()

        val response: Response = client.newCall(request).execute()
        var tmpResult = response.body()?.bytes()!!
        blFinish = true

        return tmpResult
    }

    fun getResultInputStream(strURL: String, argType: Array<String>, argValues: Array<String>): InputStream {
        blFinish = false
        val frmBody = FormBody.Builder()
        if (argType.isNotEmpty()){
            Log.d("modProtocol","[InputStream] 배열이 존재합니다.")
            for (i in 0 until argType.size) {
                frmBody.add(argType[i], argValues[i])
                Log.d("modProtocol","[InputStream] 배열 추가 : "+argType[i]+" / "+argValues[i])
            }
        }

        val requestBody: RequestBody = frmBody.build() as RequestBody

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(strURL)
            .post(requestBody)
            .build()

        val response: Response = client.newCall(request).execute()
        var tmpResult = response.body()?.byteStream()!!
        blFinish = true

        return tmpResult
    }

    fun isFinish(): Boolean{
        return blFinish
    }
}