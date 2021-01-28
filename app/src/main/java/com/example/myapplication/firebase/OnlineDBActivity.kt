package com.example.myapplication.firebase

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import com.example.myapplication.tabview.MyAdapterSaving
import com.google.android.material.tabs.TabLayout

class OnlineDBActivity: BaseActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        title = "The memo"
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tabLayout.addTab(tabLayout.newTab().setText("Save"))
        tabLayout.addTab(tabLayout.newTab().setText("Upload"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = MyAdapterSaving(
            this, supportFragmentManager,
            tabLayout.tabCount
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }
}