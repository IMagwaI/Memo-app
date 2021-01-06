package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.noteticket.view.*

class MainActivity : AppCompatActivity() {
    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        querySearch("%")
        floatingAdd.setOnClickListener {

        }

    }
    fun querySearch(search: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "description")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "Title like ?", selectionArgs, "Title")
        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                println("---------------------------------------")
                println(id.toString() + title + description)
                listNotes.add(Note(id, title, description))

            } while (cursor.moveToNext())
        }
        var myAdapter = MyNoteAdapter(this, listNotes)
        myList.adapter = myAdapter
    }

    inner class MyNoteAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(
            context: Context,
            listNotesAdapter: ArrayList<Note> = ArrayList<Note>()
        ) : super() {
            this.context = context
            this.listNotesAdapter = listNotesAdapter
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.noteticket, null)
            val note = listNotesAdapter[position]
            myView.textTitle.text = note.title
            myView.textView.text = note.description
            val selectionArgs = arrayOf(note.id.toString())

            myView.delete.setOnClickListener {
                val dbManager = DbManager(this.context!!)
                val nbr = dbManager.delete("ID=?", selectionArgs)
                if (nbr > 0)
                    Toast.makeText(this.context, "note deleted", Toast.LENGTH_LONG).show()
                querySearch("%")
            }

            myView.modify.setOnClickListener {
                goToUpdate(note)
            }



            return myView
        }


        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

        fun goToUpdate(note: Note) {
            val bundle = Bundle()
            bundle.putInt("id", note.id!!)
            bundle.putString("title", note.title)
            bundle.putString("description", note.description)
            //TODO
            /*requireView().findNavController()
                .navigate(R.id.action_notesListFragment_to_addNoteFragment, bundle)
*/
        }
    }
}