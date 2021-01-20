package com.example.myapplication


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


open class BaseActivity : AppCompatActivity() {

    private var dl: DrawerLayout? = null
    private var t: ActionBarDrawerToggle? = null
    private var nv: NavigationView? = null

      override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
         setContentView(R.layout.base_activity)
         dl = findViewById(R.id.drawer_layout)
         t = ActionBarDrawerToggle(this, dl,  R.string.drawer_open, R.string.drawer_close)
          supportActionBar?.setDisplayShowTitleEnabled(true);
          supportActionBar?.setHomeButtonEnabled(true);
          supportActionBar?.setDisplayHomeAsUpEnabled(true);
         dl?.addDrawerListener(t!!)
         t?.syncState()

         nv = findViewById(R.id.navigation_view)
        nv?.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_note -> Toast.makeText(this@BaseActivity, "My Account", Toast.LENGTH_SHORT).show()
                R.id.nav_calendar -> Toast.makeText(this@BaseActivity, "Settings", Toast.LENGTH_SHORT).show()
                R.id.nav_trash -> Toast.makeText(this@BaseActivity, "Trash", Toast.LENGTH_SHORT).show()
                else -> return@OnNavigationItemSelectedListener true
            }
            true
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (t?.onOptionsItemSelected(item) == true) {
            true
        } else super.onOptionsItemSelected(item!!)
    }

}