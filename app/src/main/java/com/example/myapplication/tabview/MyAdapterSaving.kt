package com.example.myapplication.tabview

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.note.firebase.fragments.SaveFragment
import com.example.note.firebase.fragments.UploadFragment

internal class MyAdapterSaving(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                SaveFragment()
            }
            1 -> {
                UploadFragment()
            }

            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}