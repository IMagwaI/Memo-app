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
import kotlinx.android.synthetic.main.fragment_add_draw.*
import java.io.ByteArrayOutputStream
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.drawColor
import dev.sasikanth.colorsheet.ColorSheet

class AddDrawFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_draw, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        val colors = resources.getIntArray(R.array.rainbow)

        color.setOnClickListener {
            ColorSheet().cornerRadius(8)
                .colorPicker(
                    colors = colors,
                    listener = { color -> drawColor=color
                    })
                .show((activity as FragmentActivity).supportFragmentManager)

        /* (activity as FragmentActivity).supportFragmentManager.let {
                OptionsBottomSheetFragment.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }*/
        }
        save.setOnClickListener {
            addNote()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun addNote() {
        val title: String = "Draw"
        val note: String = "This is a drawing note, press edit button to display it"
        val bitmap: Bitmap = extraBitmap

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
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