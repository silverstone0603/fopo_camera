package com.teamfopo.fopo.module

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class modDBMS(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "fopo_client"
        private val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {

        //멤버 테이블 생성
        db?.execSQL("CREATE TABLE member (id TEXT PRIMARY KEY, token TEXT, logindate TEXT, lastdate TEXT);")
        //파일목록 테이블 생성
        //db?.execSQL("CREATE TABLE files (id TEXT PRIMARY KEY, token TEXT, regdate TEXT, lastlogin TEXT);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun addUser(user: modSysData) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("id", user.mem_id)
        values.put("token", user.token)
        values.put("logindate", user.logindate)
        values.put("lastdate", user.lastdate)

        val _success = db.insert("member", null, values)
        db.close()

    return (Integer.parseInt("$_success") != -1)
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
                    temp.token = cursor.getString(1)
                    temp.logindate = cursor.getString(2)
                    temp.lastdate = cursor.getString(3)
                    //temp.mem_id = cursor.getString(cursor.getColumnIndex("id"))
                    //temp.token = cursor.getString(cursor.getColumnIndex("token"))
                    //temp.logindate = cursor.getString(cursor.getColumnIndex("logindate"))
                    //temp.lastdate = cursor.getString(cursor.getColumnIndex("lastlogin"))
                }
          //  }
        //}
        cursor.close()
        db.close()

        return temp
    }
}

class modSysData {
    var mem_id : String = ""
    var token : String = ""
    var logindate : String = ""
    var lastdate : String = ""
}