package com.example.myapplication.localdb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DbManager(context: Context) {

    val dbName="myNotes"
    val dbTable="dbNotes"
    val colId="ID"
    val colTitle="title"
    val colDescription="description"
    val colImg="img"
    val colDate="date"
    val colReminderDate="reminderdate"
    val colNotifId="notifid"
    val psswd ="password"
    val dbVersion=1
    val sqlCreateTable="CREATE TABLE IF NOT EXISTS $dbTable($colId INTEGER PRIMARY KEY,$colTitle TEXT,$colDescription TEXT,$colImg BLOB,$colReminderDate TEXT,$colDate date default CURRENT_DATE,$colNotifId TEXT,$psswd TEXT);"
    var sqlDB:SQLiteDatabase?=null

    init {
        val db=DatabaseHelperNotes(context)
        sqlDB=db.writableDatabase
    }

    inner class DatabaseHelperNotes(context: Context) :
        SQLiteOpenHelper(context, dbName, null, dbVersion) {
        var context:Context?= context

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            println("created")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("drop table if exists $dbTable")
            //db.execSQL(sqlCreateTable)
        }

    }

    fun insertNote(values: ContentValues):Long{
        val id=sqlDB!!.insert(dbTable, "", values)
        return id
    }

    fun query(
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String
    ):Cursor{
        //sqlDB!!.execSQL("drop table if exists $dbTable")
        //sqlDB!!.execSQL(sqlCreateTable)
        val db= SQLiteQueryBuilder()
        db.tables=dbTable
        val cursor=db.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor
    }

    fun delete(selection: String, selectionArgs: Array<String>):Int{
        val nbr=sqlDB!!.delete(dbTable, selection, selectionArgs)
        return nbr
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>):Int{
        val nbr =sqlDB!!.update(dbTable, values, selection, selectionArgs)
        return nbr
    }
}