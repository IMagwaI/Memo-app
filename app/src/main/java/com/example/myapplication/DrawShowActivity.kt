package com.example.myapplication

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DrawShowActivity : AppCompatActivity() {
        var id:Int?=null

        override fun onCreate(savedInstanceState: Bundle?) {
            val img=intent.getByteArrayExtra("img")
            super.onCreate(savedInstanceState)
            setContentView(R.layout.draw_show_activity)
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            toolbar.setTitleTextColor(Color.WHITE)
            setSupportActionBar(toolbar)
            println(img)
            if(img!=null) {
                id=intent.getIntExtra("id", -1)
                val opt = BitmapFactory.Options()
                opt.inMutable = true;
                extraBitmapDisplay = BitmapFactory.decodeByteArray(img, 0, img.size)
            }

        }
    }
