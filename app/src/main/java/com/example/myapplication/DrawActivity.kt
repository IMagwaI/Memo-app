package com.example.myapplication

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


class DrawActivity: AppCompatActivity() {
    var id:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        val img=intent.getByteArrayExtra("img")
        println("image = --------  "+img)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_draw)
        if(img!=null) {
            id=intent.getIntExtra("id", -1)
            val opt = BitmapFactory.Options()
            opt.inMutable = true;
            extraBitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
            println("1st bitmap: "+extraBitmap)
        }
        save.setOnClickListener {
            addNote()
        }
    }

    fun addNote() {
        var title: String? = "testDraw"
        var note: String? = "This is a drawing note, press edit button to display it"
        var bitmap: Bitmap? = extraBitmap

        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val img: ByteArray = bos.toByteArray()

        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        values.put("img", img)
        val dbManager = DbManager(this)
        if (id != 0 && id != null) {
            val selectionArgs = arrayOf(id.toString())
            val id = dbManager.update(values, "ID=?", selectionArgs)
            if (id > 0)
                Toast.makeText(this, "database updated", Toast.LENGTH_LONG).show()

        } else {
            val id = dbManager.insertNote(values)
            if (id > 0)
                Toast.makeText(this, "added to database", Toast.LENGTH_LONG).show()

        }
        dbManager.sqlDB!!.close()


    }

}
