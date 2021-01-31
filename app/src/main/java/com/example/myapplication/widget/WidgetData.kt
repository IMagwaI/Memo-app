package com.example.myapplication.widget

import android.content.Context

class WidgetData(context : Context) {

    val PREFS_NAME = "MemoappPref"
    val LOGIN_COUNT_MEMO = "LoginData"

    val preference = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
    fun getMemoCount() : Int{
        return preference.getInt(LOGIN_COUNT_MEMO,0)

    }
    fun setMemoCount(count :Int){
        val editor = preference.edit()
        editor.putInt(LOGIN_COUNT_MEMO,count)
        editor.apply()
    }

}