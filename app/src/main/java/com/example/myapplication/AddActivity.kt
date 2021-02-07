package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.fragments.*
import com.example.myapplication.fragments.reminderDate
import com.example.myapplication.tabview.MyAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.noteticket.*
import java.util.*

/**
 * This activity may contain multiple fragments
 * actualy we ve only the AddTextNoteFragment
 * later we could use another fragments to add voice or draw
 */
class AddActivity : BaseActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        title = "Add note"
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        idNote = intent.getIntExtra("id", 0)
        passwordNote = intent.getStringExtra("password")
        reminderDate=null
        if (idNote!! > 0) {
            titleNote = intent.getStringExtra("title")
            descriptionNote = intent.getStringExtra("description")
            if(intent.getStringExtra("reminderdate")!="null") {
                reminderDate= Calendar.getInstance()
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                reminderDate!!.time = sdf.parse(intent.getStringExtra("reminderdate"))
            }
        }


        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_text))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_microphone))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_pencil))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = MyAdapter(
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