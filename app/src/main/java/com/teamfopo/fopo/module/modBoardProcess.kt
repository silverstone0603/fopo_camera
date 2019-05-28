package com.teamfopo.fopo.module

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*

data class modListProcess(var brd_no: Int, var file_no: Int)
data class modViewProcess(var mem_nickname: String, var file_no: Int, var brd_content: String, var brd_like: Int)
data class modReplyProcess(var re_no: Int, var mem_nickname: String, var re_comment: String, var re_depth: Int, var re_orgin: Int, var re_date: String)

class modBoardProcess {

    inner class GetList : AsyncTask<String, Long, Array<modListProcess>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Array<modListProcess> {
            var zone_no = params[0]
            var url = "http://106.10.51.32/ajax_process/board_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","lists")
                .add("zone_no", "$zone_no")
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
                .getAsJsonObject().get("brd_lists") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, Array<modListProcess>::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: Array<modListProcess>?) {
            super.onPostExecute(result)
        }
    }

    inner class GetView : AsyncTask<String, Long, modViewProcess>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): modViewProcess {
            var idx = params[0]
            var url = "http://106.10.51.32/ajax_process/board_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","view")
                .add("brd_no","$idx")
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
                .getAsJsonObject().get("brd_view") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, modViewProcess::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: modViewProcess) {
            super.onPostExecute(result)
        }
    }

    inner class GetImage : AsyncTask<Int, Long, Bitmap>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Int?): Bitmap {
            var aa = params[0]
            var url = "http://106.10.51.32/ajax_process/photo_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "load")
                .add("file_no", "$aa")
                .build()

            val client = OkHttpClient()
            val request = Request.Builder()
                .header("Content-type","image/png")
                .url(url)
                .post(requestBody)
                .build()

            val response : Response = client.newCall(request).execute()
            var str = response.body()?.byteStream()!!
            var bit: Bitmap? = null
            bit = BitmapFactory.decodeStream(str)

            return bit!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
        }
    }

    inner class ReplyWrite : AsyncTask<String, Long, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var brd_no = params[0]
            var mem_no = params[1]
            var re_comment = params[2]
            var re_depth = params[3]
            var re_orgin = params[4]

            var url = "http://106.10.51.32/ajax_process/reply_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","write")
                .add("brd_no", "$brd_no")
                .add("mem_no", "$mem_no")
                .add("re_comment", "$re_comment")
                .add("re_depth", "$re_depth")
                .add("re_orgin", "$re_orgin")
                .build()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response : Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            return str
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }

    inner class ReplyLists : AsyncTask<String, Long, Array<modReplyProcess>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Array<modReplyProcess> {
            var brd_no = params[0]

            var url = "http://106.10.51.32/ajax_process/reply_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","lists")
                .add("brd_no", "$brd_no")
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
                .getAsJsonObject().get("reply_lists") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, Array<modReplyProcess>::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: Array<modReplyProcess>?) {
            super.onPostExecute(result)
        }
    }


    inner class ReplyDelete: AsyncTask<String, Long, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var re_no = params[0]

            var url = "http://106.10.51.32/ajax_process/reply_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","deleted")
                .add("re_no", "$re_no")
                .build()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response : Response = client.newCall(request).execute()
            var str = response.body()?.string()!!

            return str
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}