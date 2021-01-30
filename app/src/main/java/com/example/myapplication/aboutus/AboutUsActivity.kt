package com.example.myapplication.aboutus

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_aboutus.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutUsActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .enableDarkMode(false)
            .setDescription("This app has been developped by Oumaima, Ayoub and Abdelkader")
            .setImage(R.mipmap.mylogofinal)
            .addItem(Element("version : 1.0.0",null))
            .addGroup("Connect with us")
            .addEmail("familyaseds@gmail.com")
            .addWebsite("www.google.com")
            .addPlayStore("com.ideashower.readitlater.pro")
            .addGitHub("medyo")
            .create()

        mylayout.addView(aboutPage)


    }

}