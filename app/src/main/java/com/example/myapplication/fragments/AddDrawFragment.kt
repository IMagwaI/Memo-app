package com.example.myapplication.fragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.extraBitmap
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.fragment_draw.*
import java.io.ByteArrayOutputStream

class AddDrawFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draw, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        save.setOnClickListener {
            addNote()
        }
        super.onViewCreated(view, savedInstanceState)
    }
    fun addNote() {
        var title: String? = "Draw"
        var note: String? = "This is a drawing note, press edit button to display it"
        var bitmap: Bitmap? = extraBitmap

        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val img: ByteArray = bos.toByteArray()

        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        values.put("img", img)
        val dbManager = DbManager(this.requireContext())
        val id = dbManager.insertNote(values)

        dbManager.sqlDB!!.close()


    }
}