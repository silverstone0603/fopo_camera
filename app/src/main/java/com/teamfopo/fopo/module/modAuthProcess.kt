package com.teamfopo.fopo.module

import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*

data class modSessionToken(var status: String, var token: String)

class modAuthProcess {

    inner class login : AsyncTask<String, Long, modSessionToken>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): modSessionToken {
            var mem_id = params[0]
            var mem_pw = params[1]
            var url = "http://106.10.51.32/ajax_process/auth_process"
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

}