package com.example.myapplication

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @Rule
    @JvmField
    var mActivityTestRule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setUp(){
        val intent = Intent()
        mActivityTestRule.launchActivity(intent)

    }

    @Test
    fun someTest(){
        if(!mActivityTestRule.activity.isDestroyed)
            println("******************************* true")
    }

}