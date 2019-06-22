package com.teamfopo.fopo.module

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        val selectALLQuery = "SELECT * FROM 'member'"
        val cursor = db.rawQuery(selectALLQuery, null)

        val temp = modSysData()

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    temp.mem_id = cursor.getString(cursor.getColumnIndex("id"))
                    temp.mem_id = cursor.getString(cursor.getColumnIndex("token"))
                    temp.mem_id = cursor.getString(cursor.getColumnIndex("logindate"))
                    temp.mem_id = cursor.getString(cursor.getColumnIndex("lastlogin"))

                } while (cursor.moveToNext())
            }
        }
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