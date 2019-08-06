package com.teamfopo.fopo.module

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class modDBMS(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "fopo_client"
        private val DB_VERSION = 3
    }

    override fun onCreate(db: SQLiteDatabase?) {

        //멤버 테이블 생성
        db?.execSQL("CREATE TABLE member (id TEXT PRIMARY KEY, nick TEXT, token TEXT, mem_no TEXT, lastlogin TEXT);")
        //파일목록 테이블 생성
        //db?.execSQL("CREATE TABLE files (id TEXT PRIMARY KEY, token TEXT, regdate TEXT, lastlogin TEXT);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE member")
        onCreate(db)
    }

    fun addMember(user: modSysData) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("id", user.mem_id)
        values.put("nick", user.mem_nick)
        values.put("token", user.token)
        values.put("mem_no", user.mem_no)
        values.put("lastlogin", user.lastlogin)

        val success = db.insert("member", null, values)
        db.close()

    return (Integer.parseInt("$success") != -1)
}

    fun getMember() : modSysData {
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM member"
        val cursor = db.rawQuery(selectALLQuery, null)

        var temp = modSysData()

        //if (cursor != null) {
         //   if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    temp.mem_id = cursor.getString(0)
                    temp.mem_nick = cursor.getString(1)
                    temp.token = cursor.getString(2)
                    temp.mem_no = cursor.getString(3)
                    temp.lastlogin = cursor.getString(4)
                }
          //  }
        //}
        cursor.close()
        db.close()

        return temp
    }

    fun clearMember() {
        var db = readableDatabase
        db!!.execSQL("delete from member")

        db.close()
    }
}

class modSysData {
    var mem_id : String = ""
    var mem_nick : String = ""
    var token : String = ""
    var mem_no: String = ""
    var lastlogin : String = ""
}