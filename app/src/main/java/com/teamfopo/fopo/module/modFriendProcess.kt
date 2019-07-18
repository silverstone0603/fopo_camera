package com.teamfopo.fopo.module

import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.*

data class FriendsVO(var mem_no: Int, var mem_nick: String, var art_cnt: Int)

class modFriendProcess {
    inner class getFriends : AsyncTask<String, Long, List<FriendsVO>>() {
        override fun doInBackground(vararg params: String?): List<FriendsVO> {
            val url = "http://106.10.51.32/ajax_process/friend_process"

            //var mem_no = params[0]
            var mem_no = "9"

            val requestBody : RequestBody = FormBody.Builder()
                .add("type","F_list")
                .add("mem_no", "$mem_no")
                .build()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response : Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            var gson = Gson() //오브젝트 생성

            val parser = JsonParser()
            val rootObj = parser.parse(str.toString())
                .getAsJsonObject().get("fri_list") //원하는 데이터까지 찾아 들어간다.

            val turnsType = object : TypeToken<List<FriendsVO>>() {}.type
            var post = gson.fromJson<List<FriendsVO>>(rootObj, turnsType)//뭐가 들어가야 하지?

            return post
        }
    }
}