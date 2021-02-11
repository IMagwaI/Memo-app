package com.example.myapplication

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.myapplication.beans.Note
import com.example.myapplication.fragments.reminderDate
import com.example.myapplication.localdb.DbManager
import com.example.myapplication.widget.NewAppWidget
import com.example.myapplication.widget.WidgetData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custum_dialog.*
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.noteticket.*
import kotlinx.android.synthetic.main.noteticket.view.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * This is the MainActivity that show all saved notes
 */
class MainActivity : BaseActivity() {

    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
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
    @RequiresApi(Build.VERSION_CODES.N)
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
        val projections = arrayOf("ID", "title", "description", "date", "reminderdate","isNoteDeleted","password")
        val selectionArgs = arrayOf(search,"0")
        var cursor = dbManager.query(projections, "title like ? AND isNoteDeleted=?", selectionArgs, "date" + " DESC")

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun querySearch(search: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "description", "date", "reminderdate","img","isNoteDeleted","password")
        var selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "ID like ?" , selectionArgs, "date" + " DESC")
        var countmemos: Int = cursor.count
//        println(countmemos)
        val widgetdata = WidgetData(this)
        widgetdata.setMemoCount(countmemos)


        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                if(cursor.getString(cursor.getColumnIndex("isNoteDeleted"))=="0")
                    {
                    val id = cursor.getInt(cursor.getColumnIndex("ID"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val date = cursor.getString(3)
                    val reminderDate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                    val img = cursor.getBlob(cursor.getColumnIndex("img"))
                    val password = cursor.getString(cursor.getColumnIndex("password"))

                    println("data = " + id.toString() + " " + title + " " + description + " " + date + " " + reminderDate)
                   try {
                    listNotes.add(Note(id, title, description, date,img,reminderDate,password))
                }catch (e:Exception){
                    listNotes.add(Note(id, title, description, date, reminderDate,password))
                }
                }else{
                    selectionArgs=arrayOf(cursor.getInt(cursor.getColumnIndex("ID")).toString())
                    val date_limit=Calendar.getInstance()
                    date_limit.add(Calendar.DATE, 5)

                    val sdformat = SimpleDateFormat("yyyy-MM-dd");

                    var date_deleted: Date = sdformat.parse(cursor.getString(cursor.getColumnIndex("isNoteDeleted")))

                    if(date_deleted.after(date_limit.time)) {
                        dbManager.delete("ID=?", selectionArgs)
                        }
                }
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



        @RequiresApi(Build.VERSION_CODES.N)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.noteticket, null)
            val note = listNotesAdapter[position]
            if(note.description == "This is a drawing note, press the note to display it" ){
                myView.note_img.setImageResource(R.drawable.draw_icon)
            }
            myView.textTitle.text = note.title
            if(note.password == "") {
                if (note.description?.length!! > 60) {
                    myView.textView.text = note.description?.take(60) + "..."
                } else {
                    myView.textView.text = note.description
                }
            }else{
                myView.lock.visibility= View.VISIBLE
                myView.textView.text = "This note is protected with a password"
            }
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
                //Toast.makeText(this.context, "working", Toast.LENGTH_SHORT).show()
                val dbManager = DbManager(this.context!!)
                if(note.reminderdate!="null")
                //delete notification
                {
                    val projections = arrayOf("ID", "notifid")
                    var cursor = dbManager.query(
                        projections,
                        "ID like ?",
                        selectionArgs,
                        "date" + " DESC"
                    )
                    if (cursor.moveToFirst()) {
                        do {
                            val id = cursor.getString(cursor.getColumnIndex("notifid"))
                            val workManager = WorkManager.getInstance()
                            workManager.cancelWorkById(UUID.fromString(id))
                        } while (cursor.moveToNext())
                    }
                }
                //val nbr = dbManager.delete("ID=?", selectionArgs)
                val values= ContentValues()
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = formatter.format(Calendar.getInstance().time)

                values.put("isNoteDeleted",formattedDate)
                dbManager.update(values, "ID=?", selectionArgs)
                    Toast.makeText(this.context, "note deleted", Toast.LENGTH_SHORT).show()
                    if(listNotes.size==1)
                    {
                        val lastNotes = ArrayList<Note>()
                        val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                        myList.adapter = myAdapter

                        //finish()
                        //startActivity(getIntent())
                    }
                    else
                        querySearch("%")
            }

            myView.wholenote.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                if (note.password == "") {

                if (note.description == "This is a drawing note, press the note to display it") {

                    val intent = Intent(this.context, DrawShowActivity::class.java)
                    intent.putExtra("id", note.id!!)
                    intent.putExtra("title", note.title)
                    intent.putExtra("description", note.description)
                    intent.putExtra("img", note.img)

                    startActivity(intent)

                } else {
                    dialogBuilder.setTitle(note.title)
                    dialogBuilder.setMessage(note.description)

                    dialogBuilder.setIcon(R.drawable.writing_note_ready)

                    dialogBuilder.setPositiveButton("EDIT") { dialog, which ->
                        Toast.makeText(
                            applicationContext,
                            "let's edit", Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this.context, AddActivity::class.java)
                        intent.putExtra("id", note.id!!)
                        intent.putExtra("title", note.title)
                        intent.putExtra("description", note.description)
                        intent.putExtra("reminderdate", note.reminderdate)
                        intent.putExtra("password", note.password)


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

                        //delete notification
                        if(note.reminderdate!="null")
                        {
                            val projections = arrayOf("ID", "notifid")
                            var cursor = dbManager.query(
                                projections,
                                "ID like ?",
                                selectionArgs,
                                "date" + " DESC"
                            )

                            if (cursor.moveToFirst()) {
                                do {
                                    val id = cursor.getString(cursor.getColumnIndex("notifid"))
                                        val workManager = WorkManager.getInstance()
                                        workManager.cancelWorkById(UUID.fromString(id))

                                } while (cursor.moveToNext())
                            }
                        }
                        val values = ContentValues()
                        val formatter = SimpleDateFormat("yyyy-MM-dd")
                        val formattedDate = formatter.format(Calendar.getInstance().time)

                        values.put("isNoteDeleted", formattedDate)
                        dbManager.update(values, "ID=?", selectionArgs)

                        Toast.makeText(this.context, "note deleted", Toast.LENGTH_LONG).show()
                        querySearch("%")
                    }
                    dialogBuilder.show()
                }
            }else{
                    val typedpassword = EditText(this.context)
                    val dialog: AlertDialog = AlertDialog.Builder(this.context!!)
                        .setTitle("Enter your password")
                        .setView(typedpassword)
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            val checkpassword: String = typedpassword.getText().toString()
                            if (checkpassword == note.password){
                                if (note.description == "This is a drawing note, press the note to display it") {

                                    val intent = Intent(this.context, DrawShowActivity::class.java)
                                    intent.putExtra("id", note.id!!)
                                    intent.putExtra("title", note.title)
                                    intent.putExtra("description", note.description)
                                    intent.putExtra("img", note.img)

                                    startActivity(intent)

                                } else {
                                    dialogBuilder.setTitle(note.title)
                                    dialogBuilder.setMessage(note.description)
                                    dialogBuilder.setIcon(R.drawable.writing_note_ready)

                                    dialogBuilder.setPositiveButton("EDIT") { dialog, which ->
                                        val intent = Intent(this.context, AddActivity::class.java)
                                        intent.putExtra("id", note.id!!)
                                        intent.putExtra("title", note.title)
                                        intent.putExtra("description", note.description)
                                        intent.putExtra("reminderdate", note.reminderdate)
                                        intent.putExtra("password",note.password)

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
                                        //delete notification
                                        val projections = arrayOf("ID", "notifid")
                                        var cursor = dbManager.query(
                                            projections,
                                            "ID like ?",
                                            selectionArgs,
                                            "date" + " DESC"
                                        )
                                        if (cursor.moveToFirst()) {
                                            do {
                                                val id = cursor.getString(cursor.getColumnIndex("notifid"))
                                                val workManager = WorkManager.getInstance()
                                                workManager.cancelWorkById(UUID.fromString(id))
                                            } while (cursor.moveToNext())
                                        }
                                        val nbr = dbManager.delete("ID=?", selectionArgs)
                                        if (nbr > 0)
                                            Toast.makeText(this.context, "note deleted", Toast.LENGTH_LONG).show()
                                        querySearch("%")
                                    }
                                    dialogBuilder.show()
                                }

                            }else{
                                Toast.makeText(
                                    applicationContext,
                                    "Password incorrect!", Toast.LENGTH_SHORT
                                ).show()
                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                    dialog.show()

            }
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
            intent.putExtra("reminderdate",note.reminderdate)
            startActivity(intent)
        }
    }
}