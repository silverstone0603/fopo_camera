package com.teamfopo.fopo.module

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "UserDB"
        private val DB_VERSION = 1
        private val TABLE_NAME = "users"
        private val ID = "id"
        private val FIRST_NAME = "FirstName"
        private val LAST_NAME = "LastName"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME" +
                    "($ID Integer PRIMARY KEY," +
                    "$FIRST_NAME TEXT," +
                    "$LAST_NAME TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun addUser(user: Users) : Boolean {

        val db = this.writableDatabase
        val values = ContentValues()

        values.put(FIRST_NAME, user.firstName)
        values.put(LAST_NAME, user.lastName)

        val _success = db.insert(TABLE_NAME, null, values)
        db.close()

        return (Integer.parseInt("$_success") != -1)
    }

    fun getAllUser() : String {

        var allUser: String = "";
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        var id : String =""
        var firstName = ""
        var lastName = ""
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getString(cursor.getColumnIndex(ID))
                    firstName = cursor.getString(cursor.getColumnIndex(FIRST_NAME))
                    lastName = cursor.getString(cursor.getColumnIndex(LAST_NAME))

                    allUser = "$allUser \n $id $firstName $lastName"

                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return allUser
    }

}

class Users {
    var id : Int = 0
    var firstName : String = ""
    var lastName : String = ""
}