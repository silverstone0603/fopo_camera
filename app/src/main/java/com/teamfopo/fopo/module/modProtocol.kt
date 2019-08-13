package com.teamfopo.fopo.module

import okhttp3.*
import java.io.InputStream

class modProtocol{
    fun getResultString(strURL: String, argType: Array<String>, argValues: Array<String>): String {
        val frmBody = FormBody.Builder()
        if (!argType.isEmpty()){
            for (i in 0..argType.size) {
                frmBody.add(argType[i], argValues[i])
            }
        }
        frmBody.build()

        val requestBody: RequestBody = frmBody.build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(strURL)
            .post(requestBody)
            .build()

        val response: Response = client.newCall(request).execute()
        var tmpResult = response.body()?.string()!!

        return tmpResult
    }

    fun getResultByteArray(strURL: String, argType: Array<String>, argValues: Array<String>): ByteArray {
        val frmBody = FormBody.Builder()

        if (!argType.isEmpty()){
            for (i in 0..argType.size) {
                frmBody.add(argType[i], argValues[i])
            }
        }
        frmBody.build()

        val requestBody: RequestBody = frmBody.build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(strURL)
            .post(requestBody)
            .build()

        val response: Response = client.newCall(request).execute()
        var tmpResult = response.body()?.bytes()!!

        return tmpResult
    }

    fun getResultByteStream(strURL: String, argType: Array<String>, argValues: Array<String>): InputStream {
        val frmBody = FormBody.Builder()

        if (!argType.isEmpty()){
            for (i in 0..argType.size) {
                frmBody.add(argType[i], argValues[i])
            }
        }
        frmBody.build()

        val requestBody: RequestBody = frmBody.build()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(strURL)
            .post(requestBody)
            .build()

        val response: Response = client.newCall(request).execute()
        var tmpResult = response.body()?.byteStream()!!

        return tmpResult
    }
}