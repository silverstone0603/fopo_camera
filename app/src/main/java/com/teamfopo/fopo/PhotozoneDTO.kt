package com.teamfopo.fopo


import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*

data class PhotozoneDTO(var zone_no: Int, var zone_placename: String, var zone_lat: Double, var zone_lng: Double)

class modPhotoProcess {
    inner class listPhotoZone : AsyncTask<String, Long, Array<PhotozoneDTO>>() {
        override fun doInBackground(vararg params: String?): Array<PhotozoneDTO> {
            var url = "http://106.10.51.32/ajax_process/board_process"

            val requestBody : RequestBody = FormBody.Builder()
                .add("type","zone")
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
                .getAsJsonObject().get("photozone") //원하는 데이터까지 찾아 들어간다.

            var post = gson.fromJson(rootObj, Array<PhotozoneDTO>::class.java)//뭐가 들어가야 하지?

            return post
        }

        override fun onPostExecute(result: Array<PhotozoneDTO>?) {
            super.onPostExecute(result)
            Log.d("ㅇㅇ","요청 성공쓰 ^^")
        }
    }
}