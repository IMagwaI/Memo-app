package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.fragment_add_note.*

class AddTextNoteFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    var id: Int? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        saveButton.setOnClickListener {
            addNote()
        }
        //check edit
        id = arguments?.getInt("id")
        println("---------------$id")
        if (id != 0 && id != null) {
            val title = arguments?.getString("title")
            titleText.setText(title)
            val description = arguments?.getString("description")
            multiLineText.setText(description)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    fun addNote() {
        val title: String? = titleText.text.toString()
        val note: String? = multiLineText.text.toString()
        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        val dbManager = DbManager(this.requireActivity())
        if (id != 0 && id != null) {
            val selectionArgs = arrayOf(id.toString())
            val id = dbManager.update(values, "ID=?", selectionArgs)
            if (id > 0)
                Toast.makeText(this.requireContext(), "database updated", Toast.LENGTH_LONG).show()

        } else {
            val id = dbManager.insertNote(values)
            if (id > 0)
                Toast.makeText(this.requireContext(), "added to database", Toast.LENGTH_LONG).show()

        }
    }

}