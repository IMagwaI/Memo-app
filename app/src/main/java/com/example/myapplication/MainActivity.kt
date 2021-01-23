package com.example.myapplication

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.RoomMasterTable.TABLE_NAME
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custum_dialog.*
import kotlinx.android.synthetic.main.noteticket.*
import kotlinx.android.synthetic.main.noteticket.view.*


/**
 * This is the MainActivity that show all saved notes
 */
class MainActivity : BaseActivity() {

    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshTodayLabel()


    }


    /**
     * get data after each activity onStart callback
     */
    fun refreshTodayLabel() {
        val man = AppWidgetManager.getInstance(this)
        val ids = man.getAppWidgetIds(ComponentName(this, NewAppWidget::class.java))
        val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(updateIntent)
    }
    override fun onStart() {
        super.onStart()
        refreshTodayLabel()
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
        val projections = arrayOf("ID", "title", "description", "date", "reminderdate")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "title like ?", selectionArgs, "date" + " DESC")
        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val date = cursor.getString(3)
                val reminderdate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                listNotes.add(Note(id, title, description, date, reminderdate))
            } while (cursor.moveToNext())
        }
        cursor.close()
        dbManager.sqlDB?.close()
        var myAdapter = MyNoteAdapter(this, listNotes)
        myList.adapter = myAdapter
    }

    fun querySearch(search: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "description", "date", "reminderdate")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "ID like ?", selectionArgs, "date" + " DESC")
        var countmemos: Int = cursor.getCount()
//        println(countmemos)
        val widgetdata = WidgetData(this)
        widgetdata.setMemoCount(countmemos)


        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val date = cursor.getString(3)
                val reminderDate = cursor.getString(cursor.getColumnIndex("reminderdate"))

                println("data = " + id.toString() + " " + title + " " + description + " " + date + " " + reminderDate)

                listNotes.add(Note(id, title, description, date, reminderDate))

            } while (cursor.moveToNext())
        }
        cursor.close()
        var myAdapter = MyNoteAdapter(this, listNotes)
        myList.adapter = myAdapter
    }

//fun showAlertDialogButtonClicked(view: View?) {
//
//    // setup the alert builder
//    val builder = AlertDialog.Builder(this)
//    builder.setTitle("AlertDialog")
//    builder.setMessage("Would you like to continue learning how to use Android alerts?")
//
//    // add the buttons
//    builder.setPositiveButton("Continue", null)
//    builder.setNegativeButton("Cancel", null)
//
//    // create and show the alert dialog
//    val dialog = builder.create()
//    dialog.show()
//}
    //////////////////////
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
            myView.date.text = note.date
            val selectionArgs = arrayOf(note.id.toString())
            val dbManager = DbManager(this.context!!)
///////////////////////// testing
            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    var pos = viewHolder.adapterPosition
                    val nbr = dbManager.delete("ID=?", selectionArgs)
                }
            }

//////////////////////////end testing
            myView.delete.setOnClickListener {
                Toast.makeText(this.context, "working", Toast.LENGTH_SHORT).show()
                val dbManager = DbManager(this.context!!)
                val nbr = dbManager.delete("ID=?", selectionArgs)
                if (nbr > 0)
                    Toast.makeText(this.context, "note deleted", Toast.LENGTH_SHORT).show()
                querySearch("%")
            }

            myView.wholenote.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)


                if (note.description == "This is a drawing note, press edit button to display it") {
//                    builder.setTitle("This is a draw memo")

//                    val c = db.rawQuery("select * from img", null)
//                    if (c.moveToNext())
//                    {
//                        val image = c.getBlob(0)
//                        val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
//                        imageView.setImageBitmap(bmp)
//                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
//                    }
                } else {
                    dialogBuilder.setTitle(note.title)
                    dialogBuilder.setMessage(note.description)

//                    dialogBuilder.setView(R.layout.custum_dialog)

                    dialogBuilder.setIcon(R.drawable.garfield)
//                    titleNote.text = Editable.Factory.getInstance().newEditable(note.title)
//                    titleNote.setText(note.title)
//                    textNote.setText(note.description)
//                    BtnUpdate.setOnClickListener {
//                        val newtitle = titleNote.text.toString()
//                        val newmsg = textNote.text.toString()
//                        val values = ContentValues()
//                        values.put("title", newtitle)
//                        values.put("description", newmsg)
//                        val dbManager = DbManager(this@MainActivity)
//                        if (idNote != 0 && idNote != null) {
//                            val selectionArgs = arrayOf(idNote.toString())
//                            val id = dbManager.update(values, "ID=?", selectionArgs)
//                            if (id > 0)
//                                Toast.makeText(this@MainActivity, "database updated", Toast.LENGTH_LONG).show()
//
//                        }
//
//                    }
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
                    dialogBuilder.setPositiveButton("EDIT") { dialog, which ->
                        Toast.makeText(
                            applicationContext,
                            "let's edit", Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this.context, AddActivity::class.java)
                        intent.putExtra("id", note.id!!)
                        intent.putExtra("title", note.title)
                        intent.putExtra("description", note.description)
                        startActivity(intent)
                    }

                    dialogBuilder.setNegativeButton("CANCEL") { dialog, which ->
                        Toast.makeText(
                            applicationContext,
                            android.R.string.no, Toast.LENGTH_SHORT
                        ).show()

                    }

                    dialogBuilder.setNeutralButton("DELETE") { dialog, which ->
                        Toast.makeText(
                            applicationContext,
                            "DELETED", Toast.LENGTH_SHORT
                        ).show()
                        val dbManager = DbManager(this.context!!)
                        val nbr = dbManager.delete("ID=?", selectionArgs)
                        if (nbr > 0)
                            Toast.makeText(this.context, "note deleted", Toast.LENGTH_LONG).show()
                        querySearch("%")
                    }
                    dialogBuilder.show()
//                Toast.makeText(this.context, "working touch", Toast.LENGTH_LONG).show()
//                val intent = Intent(this.context, NoteViewer::class.java)
//                intent.putExtra("id", note.id!!)
//                intent.putExtra("title", note.title)
//                intent.putExtra("description", note.description)
//                intent.putExtra("img" ,note.img)
//                startActivity(intent)
                }
            }
            myView.modify.setOnClickListener {
                goToUpdate(note)
            }
            if (note.reminderdate != "null")
                myView.reminder.visibility = View.VISIBLE
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