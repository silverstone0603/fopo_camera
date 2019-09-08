package com.teamfopo.fopo.module

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*

data class modListProcess(var brd_no: Int, var file_no: Int)
data class modViewProcess(var mem_nick: String, var file_no: Int, var brd_content: String, var brd_like: Int)
data class modReplyProcess(var re_no: Int, var mem_no: Int, var mem_nick: String, var rre_no: Int, var re_comment: String, var re_date: String)

class modBoardProcess {
    inner class GetList : AsyncTask<String, Long, Array<modListProcess>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Array<modListProcess> {
            var zone_no = params[0]
            var url = "http://106.10.51.32/process/board_process"
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
            var url = "http://106.10.51.32/process/board_process"
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
            var url = "http://106.10.51.32/process/photo_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("photo_type", "load")
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
            var zone_no = params[0]
            var brd_no = params[1]
            var mem_no = params[2]
            var rre_no = params[3]
            var re_comment = params[4]
            var token = FOPOService.dataMemberVO!!.token

            var url = "http://106.10.51.32/process/reply_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","write")
                .add("zone_no", "$zone_no")
                .add("brd_no", "$brd_no")
                .add("mem_no", "$mem_no")
                .add("rre_no", "$rre_no")
                .add("re_comment", "$re_comment")
                .add("token", "$token")
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

            var url = "http://106.10.51.32/process/reply_process"
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
            var token = FOPOService.dataMemberVO!!.token

            var url = "http://106.10.51.32/process/reply_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","deleted")
                .add("re_no", "$re_no")
                .add("token", "$token")
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

    inner class Write: AsyncTask<String, Long, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var mem_no = params[0]
            var zone_no = params[1]
            var brd_content = params[2]
            var filedata = params[3]
            var token = FOPOService.dataMemberVO!!.token

            var url = "http://106.10.51.32/process/board_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","write")
                .add("mem_no", "$mem_no")
                .add("zone_no", "$zone_no")
                .add("brd_content", "$brd_content")
                .add("filedata", "$filedata")
                .add("token", "$token")
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

    inner class getFriendArticles: AsyncTask<String, Long, Array<modListProcess>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Array<modListProcess> {
            var mem_no = params[0]

            var url = "http://106.10.51.32/process/board_process"
            val requestBody : RequestBody = FormBody.Builder()
                .add("type","f_lists")
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
                .getAsJsonObject().get("brd_lists") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, Array<modListProcess>::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: Array<modListProcess>?) {
            super.onPostExecute(result)
        }
    }

    inner class getZoneNumber: AsyncTask<String, Long, String>() {
        override fun doInBackground(vararg params: String?): String {
            var tmpBrdNo = params[0]
            //var gps_latitude = params[0]
            //var gps_longitude = params[1]

            var url = "http://106.10.51.32/process/board_process"
            val requestBody: RequestBody = FormBody.Builder()
                .add("type", "photozone")
                .add("brd_no", "$tmpBrdNo")
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