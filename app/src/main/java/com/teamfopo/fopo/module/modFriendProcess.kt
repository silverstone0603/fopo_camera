package com.teamfopo.fopo.module

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.google.vr.dynamite.client.f
import okhttp3.*

data class FriendsVO(var mem_no: Int, var mem_nick: String, var art_cnt: Int, var mem_picfile: Int, var bmProfile: Bitmap)
data class UserVO(var mem_no: Int, var mem_nick: String, var mem_picfile: Int, var art_cnt: Int, var status: Int)
data class FriendArticleVO(var mem_no: Int)

class modFriendProcess {
    inner class getFriends : AsyncTask<String, Long, MutableList<FriendsVO>>() {
        override fun doInBackground(vararg params: String?): MutableList<FriendsVO> {
            val url = "http://106.10.51.32/ajax_process/friend_process"
            //var mem_no = params[0]
            var mem_no = FOPOService.dataMemberVO!!.mem_no

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

            val turnsType = object : TypeToken<MutableList<FriendsVO>>() {}.type
            var post = gson.fromJson<MutableList<FriendsVO>>(rootObj, turnsType)//뭐가 들어가야 하지?

            return post
        }
    }

    inner class findUser : AsyncTask<String, Long, UserVO>() {
        override fun doInBackground(vararg params: String?): UserVO {
            val url = "http://106.10.51.32/ajax_process/friend_process"
            var mem_no = FOPOService.dataMemberVO!!.mem_no
            var mem_id = params[0]

            val requestBody : RequestBody = FormBody.Builder()
                .add("type","F_find")
                .add("mem_no", "$mem_no")
                .add("mem_id", "$mem_id")
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
            val rootObj = parser.parse(str)
                .getAsJsonObject().get("user_find") //원하는 데이터까지 찾아 들어간다.

            try {
                var post = gson.fromJson(rootObj, UserVO::class.java)//뭐가 들어가야 하지?
                return post
            } catch (e: Exception) { return UserVO(0,"null",0,0, 1) }
            finally {
            }
        }
    }

    inner class addFriend : AsyncTask<String, Long, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = "http://106.10.51.32/ajax_process/friend_process"
            val mem_no = FOPOService.dataMemberVO!!.mem_no
            val fri_no = params[0]

            val requestBody : RequestBody = FormBody.Builder()
                .add("type","F_add")
                .add("mem_no", "$mem_no")
                .add("fri_no", "$fri_no")
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
            val rootObj = parser.parse(str)
                .getAsJsonObject().get("status") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, String::class.java)//뭐가 들어가야 하지?

            return post
        }
    }

    inner class removeFriend : AsyncTask<String, Long, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = "http://106.10.51.32/ajax_process/friend_process"
            val mem_no = FOPOService.dataMemberVO!!.mem_no
            val fri_no = params[0]

            val requestBody : RequestBody = FormBody.Builder()
                .add("type","F_delete")
                .add("mem_no", "$mem_no")
                .add("fri_no", "$fri_no")
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
            val rootObj = parser.parse(str)
                .getAsJsonObject().get("status") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, String::class.java)//뭐가 들어가야 하지?

            return post
        }
    }
}