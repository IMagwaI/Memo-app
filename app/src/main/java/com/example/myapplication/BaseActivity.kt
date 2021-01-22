package com.example.myapplication


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myapplication.firebase.LoginActivity
import com.google.android.material.navigation.NavigationView


open class BaseActivity : AppCompatActivity() {
    //var toolbar: Toolbar? = null
    var drawerLayout: DrawerLayout? = null
    var drawerToggle: ActionBarDrawerToggle? = null
    var navigationView: NavigationView? = null
    var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this@BaseActivity
        setContentView(R.layout.base_activity)
    }

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.base_activity, null) as DrawerLayout
        val activityContainer = fullView.findViewById<View>(R.id.activity_content) as FrameLayout
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)
    }



    private fun setUpNav() {
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawerToggle = ActionBarDrawerToggle(
            this@BaseActivity,
            drawerLayout,
            R.string.app_name,
            R.string.app_name
        )
        drawerLayout!!.setDrawerListener(drawerToggle)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        navigationView = findViewById<View>(R.id.navigation_view) as NavigationView


        // Setting Navigation View Item Selected Listener to handle the item
        // click of the navigation menu
        navigationView!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem -> // Checking if the item is in checked state or not, if not make
            // it in checked state
            if (menuItem.isChecked) menuItem.isChecked = false else menuItem.isChecked = true

            // Closing drawer on item click
            drawerLayout!!.closeDrawers()

            // Check to see which item was being clicked and perform
            // appropriate action
            val intent_calendar = Intent(this, CalendarActivity::class.java)
            val intent_add_note = Intent(this, AddActivity::class.java)
            val intent_note = Intent(this, MainActivity::class.java)
            val intent_login = Intent(this, LoginActivity::class.java)

            when (menuItem.itemId) {
                R.id.nav_note -> this.startActivity(intent_note)
                R.id.nav_calendar -> this.startActivity(intent_calendar)
                R.id.nav_trash -> Toast.makeText(this, "Trash", Toast.LENGTH_SHORT).show()
                R.id.nav_add_note -> this.startActivity(intent_add_note)
                R.id.nav_sync-> this.startActivity(intent_login)
                else -> return@OnNavigationItemSelectedListener true
            }
            false
        })

        // calling sync state is necessay or else your hamburger icon wont show
        // up
        drawerToggle!!.syncState()
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setUpNav()
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig != null) {
            super.onConfigurationChanged(newConfig)
        }
        drawerToggle!!.onConfigurationChanged(newConfig)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle?.onOptionsItemSelected(item) == true) {
            true
        } else super.onOptionsItemSelected(item!!)
    }
}