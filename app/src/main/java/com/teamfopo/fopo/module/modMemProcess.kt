package com.teamfopo.fopo.module

import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*

data class MyInfoVO(val mem_nick: String, val mem_email: String, val mem_phone: String, val mem_picfile: Int)
data class InfoChangeVO(val status: String)

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
                .add("type", "signup")
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

    inner class myInfo : AsyncTask<String, Long, MyInfoVO>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): MyInfoVO {
            val mem_no = params[0]

            var url = "http://106.10.51.32/ajax_process/member_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "info")
                .add("mem_no","$mem_no")
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
            val rootObj = parser.parse(str.toString())

            var post = gson.fromJson(rootObj, MyInfoVO::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: MyInfoVO?) {
            super.onPostExecute(result)
        }
    }


    inner class infomodify : AsyncTask<String, Long, InfoChangeVO>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): InfoChangeVO {
            var mem_no = params[0]
            var profile = params[1]
            var mem_nick = params[2]
            var mem_newpw = params[3]
            var mem_phone = params[4]


            var url = "http://106.10.51.32/ajax_process/member_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "modify")
                .add("mem_no", "$mem_no")
                .add("profile", "$profile")
                .add("mem_nick", "$mem_nick")
                .add("mem_newpw", "$mem_newpw")
                .add("mem_phone", "$mem_phone")
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

            var post = gson.fromJson(rootObj, InfoChangeVO::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: InfoChangeVO?) {
            super.onPostExecute(result)
        }
    }
}