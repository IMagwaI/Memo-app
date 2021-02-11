package com.example.myapplication


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myapplication.aboutus.AboutUsActivity
import com.example.myapplication.firebase.LoginActivity
import com.google.android.material.navigation.NavigationView


open class BaseActivity : AppCompatActivity() {
    //var toolbar: Toolbar? = null
    var drawerLayout: DrawerLayout? = null
    var drawerToggle: ActionBarDrawerToggle? = null
    var navigationView: NavigationView? = null
    var mContext: Context? = null
    var darkModeSwitch: SwitchCompat? = null
    var sharedPreferences: SharedPreferences? = null

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
        drawerLayout!!.addDrawerListener(drawerToggle!!)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        navigationView = findViewById<View>(R.id.navigation_view) as NavigationView

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        val isDarkModeOn = sharedPreferences!!.getBoolean("isDarkModeOn", false)
        // Setting Navigation View Item Selected Listener to handle the item
        darkModeSwitch =
            navigationView!!.menu.findItem(R.id.nav_darkmode_id).actionView as SwitchCompat
        darkModeSwitch!!.isChecked=isDarkModeOn
        if(isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            navigationView!!.menu.findItem(R.id.nav_darkmode_id).icon =
                ContextCompat.getDrawable(this, R.drawable.ic_night);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            navigationView!!.menu.findItem(R.id.nav_darkmode_id).title = "Day Mode"
        }
        darkModeSwitch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            //your action
            if (!isChecked) {
                // Switch is unchecked - DAY MODE
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                navigationView!!.menu.findItem(R.id.nav_darkmode_id).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_light);
                navigationView!!.menu.findItem(R.id.nav_darkmode_id).title ="Day Mode"
                editor.putBoolean(
                    "isDarkModeOn", false
                );
                editor.apply();
            } else {
                // Switch is checked - NIGHT MODE

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                navigationView!!.menu.findItem(R.id.nav_darkmode_id).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_night);
                navigationView!!.menu.findItem(R.id.nav_darkmode_id).title ="Dark Mode"
                editor.putBoolean(
                    "isDarkModeOn", true
                );
                editor.apply();
            }
        })

        // click of the navigation menu
        navigationView!!.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem -> // Checking if the item is in checked state or not, if not make
            // it in checked state
            menuItem.isChecked = !menuItem.isChecked

            // Closing drawer on item click
            drawerLayout!!.closeDrawers()

            // Check to see which item was being clicked and perform
            // appropriate action
            val intent_calendar = Intent(this, CalendarActivity::class.java)
            intent_calendar.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val intent_add_note = Intent(this, AddActivity::class.java)
            intent_add_note.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val intent_note = Intent(this, MainActivity::class.java)
            intent_note.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val intent_login = Intent(this, LoginActivity::class.java)
            intent_login.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val intent_aboutus = Intent(this, AboutUsActivity::class.java)
            intent_aboutus.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val intent_trash= Intent(this, TrashActivity::class.java)
            intent_aboutus.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            when (menuItem.itemId) {
                R.id.nav_note -> this.startActivity(intent_note)
                R.id.nav_calendar -> this.startActivity(intent_calendar)
                R.id.nav_trash -> this.startActivity(intent_trash)
                R.id.nav_add_note -> this.startActivity(intent_add_note)
                R.id.nav_sync -> this.startActivity(intent_login)
                R.id.nav_AboutUs -> this.startActivity(intent_aboutus)
                R.id.nav_trash -> this.startActivity(intent_trash)
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
        } else super.onOptionsItemSelected(item)
    }
}