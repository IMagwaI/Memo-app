package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.noteticket.view.*

/**
 * This is the MainActivity that show all saved notes
 */
class MainActivity : AppCompatActivity() {
    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    /**
     * get data after each acticity onStart callback
     */
    override fun onStart() {
        super.onStart()
        querySearch("%")
        floatingAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    // search
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu?.findItem(R.id.nav_search)
        if (searchItem != null) {
            val seachView = searchItem.actionView as SearchView
            seachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    if (p0!!.isNotEmpty()) {
                        val key = "%" + p0 + "%"
                        searchBar(key)
                    } else {
                        searchBar("%")
                    }
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun searchBar(search: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "description", "date")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "title like ?", selectionArgs, "date" + " DESC")
        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val date = cursor.getString(3)
                listNotes.add(Note(id, title, description, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        var myAdapter = MyNoteAdapter(this, listNotes)
        myList.adapter = myAdapter
    }

    fun querySearch(search: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "description", "date")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "ID like ?", selectionArgs, "date" + " DESC")
        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val date = cursor.getString(3)

                println("data = " + id.toString() + " " + title + " " + description + " " + date)

                listNotes.add(Note(id, title, description, date))

            } while (cursor.moveToNext())
        }
        cursor.close()
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

        /**
         * add a note 'noteticket layout' view for each item in the list
         */
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.noteticket, null)
            val note = listNotesAdapter[position]
            myView.textTitle.text = note.title
            myView.textView.text = note.description
            myView.date.text = note.date
            val selectionArgs = arrayOf(note.id.toString())
            val dbManager = DbManager(this.context!!)
///////////////////////// testing
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    var pos = viewHolder.adapterPosition
                    val nbr = dbManager.delete("ID=?", selectionArgs)
                }
            }

//////////////////////////end testing
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

        /**
         * this method runs after clicking on the modify button corresponding to one of the displayed note
         * it ll start the AddActivity to modify the note
         */
        fun goToUpdate(note: Note) {
            val intent = Intent(this.context, AddActivity::class.java)
            intent.putExtra("id", note.id!!)
            intent.putExtra("title", note.title)
            intent.putExtra("description", note.description)
            startActivity(intent)
        }
    }
}