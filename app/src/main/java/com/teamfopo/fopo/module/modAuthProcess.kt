package com.teamfopo.fopo.module

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*

data class modSessionToken(var status: String, var token: String, var mem_no: String, var mem_nick: String)
data class webLoginVO(var status: String, var sess_no: Int, var sess_verify: Int)
data class AuthCheckVO(var status: String)

class modAuthProcess {

    inner class login : AsyncTask<String, Long, modSessionToken>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

            override fun doInBackground(vararg params: String?): modSessionToken {
            var mem_id = params[0]
            var mem_pw = params[1]
            var url = "http://106.10.51.32/process/auth_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("id", "$mem_id")
                .add("pw", "$mem_pw")
                .build()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            var gson = Gson() //오브젝트 생성

            val parser = JsonParser()
            val rootObj = parser.parse(str)
                //.getAsJsonObject().get("session_list") //mem_list까지 들어가라.

            var post = gson.fromJson(rootObj, modSessionToken::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: modSessionToken?) {
            super.onPostExecute(result)
        }
    }

    inner class web_auth : AsyncTask<String, Long, webLoginVO>() {
        override fun doInBackground(vararg params: String?): webLoginVO {
            var url = "http://106.10.51.32/process/web_process"
            var type = "web_auth"
            var mem_no = params[0]
            var sess_devicetype = params[1]

            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "$type")
                .add("mem_no", "$mem_no")
                .add("sess_devicetype", "$sess_devicetype")
                .build()
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            var gson = Gson() //오브젝트 생성

            val parser = JsonParser()
            val rootObj = parser.parse(str)



            var post = gson.fromJson(rootObj, webLoginVO::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: webLoginVO?) {
            super.onPostExecute(result)
        }
    }

    inner class auth_check : AsyncTask<String, Long, AuthCheckVO>() {
        override fun doInBackground(vararg params: String?): AuthCheckVO {
            var url = "http://106.10.51.32/process/web_process"
            var type = "auth_check"
            var sess_no = params[0]

            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "$type")
                .add("sess_no", "$sess_no")
                .build()
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            var gson = Gson() //오브젝트 생성

            val parser = JsonParser()
            val rootObj = parser.parse(str)

            var post = gson.fromJson(rootObj, AuthCheckVO::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: AuthCheckVO?) {
            super.onPostExecute(result)
        }
    }

    inner class testim : AsyncTask<String, Long, String>() {
        override fun doInBackground(vararg params: String?): String {
            var url = "http://106.10.51.32/process/web_process"
            var type = "testim"
            var sess_no = params[0]

            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "$type")
                .add("sess_no", "$sess_no")
                .build()
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            return str
        }
    }

    inner class logout : AsyncTask<String, Long, String>() {
        override fun doInBackground(vararg params: String?): String {
            var url = "http://106.10.51.32/process/web_process"
            var type = "logout"
            var token = params[0]

            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "$type")
                .add("token", "$token")
                .build()
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            return str
        }
    }
}