package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class AddActivity : FragmentActivity() {
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val fm: FragmentManager = supportFragmentManager
        fragment = fm.findFragmentByTag("fragment_add_note")
        if (fragment == null) {
            val ft: FragmentTransaction = fm.beginTransaction()
            fragment = AddNoteFragment()
            ft.add(android.R.id.content, AddNoteFragment(), "fragment_add_note")
            ft.commit()
        }

    }
}