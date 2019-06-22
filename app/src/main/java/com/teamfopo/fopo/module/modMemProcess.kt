package com.teamfopo.fopo.module

import android.os.AsyncTask
import okhttp3.*

class modMemProcess {

    inner class signUp : AsyncTask<String, Long, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var mem_id = params[0]
            var mem_pw = params[1]
            var mem_nick = params[2]
            var mem_gender = params[3]
            var url = "http://106.10.51.32/ajax_process/member_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "signUp")
                .add("mem_id", "$mem_id")
                .add("mem_pw", "$mem_pw")
                .add("mem_nick", "$mem_nick")
                .add("mem_gender", "$mem_gender")
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

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }

}