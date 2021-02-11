package com.example.myapplication

import android.appwidget.AppWidgetManager
import android.content.*
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import com.example.myapplication.notif.NotificationSchedule
import com.example.myapplication.widget.NewAppWidget
import com.example.myapplication.widget.WidgetData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.noteticket.view.*
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList

class TrashActivity:BaseActivity() {
    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.title = "Corbeille"
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
        var cursor = dbManager.query(projections, "title like ? AND isNoteDeleted != ?", selectionArgs, "date" + " DESC")
        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val date = cursor.getString(3)
                val reminderdate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                val password = cursor.getString(cursor.getColumnIndex("password"))
                listNotes.add(Note(id, title, description, date, reminderdate,password))
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
        var selectionArgs = arrayOf(search,"0")
        var cursor = dbManager.query(projections, "ID like ? AND isNoteDeleted != ?", selectionArgs, "date" + " DESC")
        var countmemos: Int = cursor.count
//        println(countmemos)
        val widgetdata = WidgetData(this)
        widgetdata.setMemoCount(countmemos)


        if (cursor.moveToFirst()) {
            listNotes.clear()
            do {
                val deletedDate= cursor.getString(cursor.getColumnIndex("isNoteDeleted"))

                val date_limit=Calendar.getInstance()
                date_limit.add(Calendar.DATE, 5)

                val sdformat = SimpleDateFormat("yyyy-MM-dd");

                var date_deleted: Date = sdformat.parse(deletedDate)
                print("RESUTAT" +(date_deleted > date_limit.time) )
                selectionArgs=arrayOf(cursor.getInt(cursor.getColumnIndex("ID")).toString())
                if(date_deleted.after(date_limit.time)) {
                    dbManager.delete("ID=?", selectionArgs)
                }else {
                    val id = cursor.getInt(cursor.getColumnIndex("ID"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val date = cursor.getString(3)
                    val reminderDate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                    val img = cursor.getBlob(cursor.getColumnIndex("img"))
                    val password = cursor.getString(cursor.getColumnIndex("password"))

                    try {
                        listNotes.add(Note(id, title, description, date, img, reminderDate,password))
                    } catch (e: Exception) {
                        listNotes.add(Note(id, title, description, date, reminderDate,password))
                    }
                }

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
        @RequiresApi(Build.VERSION_CODES.N)
        fun scheduleNotification(timeDelay: Long, tag: String, body: String) {

            var data = Data.Builder().putString("body", body).putString("title",tag)

            val work = OneTimeWorkRequestBuilder<NotificationSchedule>()
                .setInitialDelay(timeDelay, java.util.concurrent.TimeUnit.SECONDS)
                .setConstraints(
                    Constraints.Builder().setTriggerContentMaxDelay(
                        1,
                        java.util.concurrent.TimeUnit.SECONDS
                    ).build()
                ) // API Level 24
                .setInputData(data.build())
                .addTag(tag)
                .build()

            WorkManager.getInstance().enqueue(work)

        }


        @RequiresApi(Build.VERSION_CODES.N)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.noteticket, null)
            val note = listNotesAdapter[position]
            if(note.description == "This is a drawing note, press the note to display it" ){
                myView.note_img.setImageResource(R.drawable.draw_icon)
            }
            //restore a note
            myView.restore.visibility = View.VISIBLE
            myView.restore.setOnClickListener{
                val values= ContentValues()
                values.put("isNoteDeleted","0")
                val dbManager = DbManager(this.context!!)
                val selectionArgs= arrayOf(note.id.toString())
                dbManager.update(values, "ID=?", selectionArgs)
                if(note.reminderdate!="null") {
                    val cal = Calendar.getInstance()
                    val sdf = SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH)
                    cal.time = sdf.parse(note.reminderdate)
                    var delay: Long = abs(System.currentTimeMillis() - cal!!.time.time)
                    println("this is delay" + delay)
                    scheduleNotification(delay / 1000, note.title!!, note.description!!)
                }
                if(listNotes.size==1)
                {
                    val lastNotes = ArrayList<Note>()
                    val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                    myList.adapter = myAdapter
                }else
                querySearch("%")
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
                val nbr = dbManager.delete("ID=?", selectionArgs)
                if (nbr > 0)
                    Toast.makeText(this.context, "note deleted permanatly", Toast.LENGTH_SHORT).show()
                if(listNotes.size==1)
                {
                    val lastNotes = ArrayList<Note>()
                    val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                    myList.adapter = myAdapter

                    //finish()
                    //startActivity(getIntent())
                }
                else querySearch("%")
            }

            myView.wholenote.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(this@TrashActivity)

                if(note.password=="") {
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
                        //restore
                        dialogBuilder.setPositiveButton("RESTORE") { dialog, which ->
                            Toast.makeText(
                                applicationContext,
                                "note restored", Toast.LENGTH_SHORT
                            ).show()
                            val values = ContentValues()
                            values.put("isNoteDeleted", 0)
                            val dbManager = DbManager(this.context!!)
                            dbManager.update(values, "ID=?", selectionArgs)
                            if (note.reminderdate != "null") {
                                val cal = Calendar.getInstance()
                                val sdf =
                                    SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                                cal.time = sdf.parse(note.reminderdate)
                                var delay: Long = abs(System.currentTimeMillis() - cal!!.time.time)
                                println("this is delay" + delay)
                                scheduleNotification(delay / 1000, note.title!!, note.description!!)
                            }
                            if (listNotes.size == 1) {
                                val lastNotes = ArrayList<Note>()
                                val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                                myList.adapter = myAdapter

                            } else
                                querySearch("%")
                        }

                        dialogBuilder.setNegativeButton("CANCEL") { dialog, which ->
                            Toast.makeText(
                                applicationContext,
                                android.R.string.no, Toast.LENGTH_SHORT
                            ).show()

                        }

                        dialogBuilder.setNeutralButton("DELETE PERMANENTLY") { dialog, which ->
                            Toast.makeText(
                                applicationContext,
                                "DELETED", Toast.LENGTH_SHORT
                            ).show()
                            val dbManager = DbManager(this.context!!)
                            //delete notification
                            if (note.reminderdate != "null")
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
                            val nbr = dbManager.delete("ID=?", selectionArgs)
                            if (nbr > 0)
                                Toast.makeText(
                                    this.context,
                                    "note deleted permanently",
                                    Toast.LENGTH_LONG
                                ).show()
                            if (listNotes.size == 1) {
                                val lastNotes = ArrayList<Note>()
                                val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                                myList.adapter = myAdapter

                                //finish()
                                //startActivity(getIntent())
                            } else
                                querySearch("%")
                        }
                        dialogBuilder.show()
                    }
                }else{val typedpassword = EditText(this.context)
                    val dialog: AlertDialog = AlertDialog.Builder(this.context!!)
                        .setTitle("Enter your password")
                        .setView(typedpassword)
                        .setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                val checkpassword: String = typedpassword.getText().toString()
                                if (checkpassword == note.password) {
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
                                        //restore
                                        dialogBuilder.setPositiveButton("RESTORE") { dialog, which ->
                                            Toast.makeText(
                                                applicationContext,
                                                "restore the note", Toast.LENGTH_SHORT
                                            ).show()
                                            val values = ContentValues()
                                            values.put("isNoteDeleted", 0)
                                            val dbManager = DbManager(this.context!!)
                                            dbManager.update(values, "ID=?", selectionArgs)
                                            if (note.reminderdate != "null") {
                                                val cal = Calendar.getInstance()
                                                val sdf =
                                                    SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                                                cal.time = sdf.parse(note.reminderdate)
                                                var delay: Long = abs(System.currentTimeMillis() - cal!!.time.time)
                                                println("this is delay" + delay)
                                                scheduleNotification(delay / 1000, note.title!!, note.description!!)
                                            }
                                            if (listNotes.size == 1) {
                                                val lastNotes = ArrayList<Note>()
                                                val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                                                myList.adapter = myAdapter

                                            } else
                                                querySearch("%")
                                        }

                                        dialogBuilder.setNegativeButton("CANCEL") { dialog, which ->
                                            Toast.makeText(
                                                applicationContext,
                                                android.R.string.no, Toast.LENGTH_SHORT
                                            ).show()

                                        }

                                        dialogBuilder.setNeutralButton("DELETE PERMANENTLY") { dialog, which ->
                                            Toast.makeText(
                                                applicationContext,
                                                "DELETED", Toast.LENGTH_SHORT
                                            ).show()
                                            val dbManager = DbManager(this.context!!)
                                            //delete notification
                                            if (note.reminderdate != "null")
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
                                            val nbr = dbManager.delete("ID=?", selectionArgs)
                                            if (nbr > 0)
                                                Toast.makeText(
                                                    this.context,
                                                    "note deleted permanently",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            if (listNotes.size == 1) {
                                                val lastNotes = ArrayList<Note>()
                                                val myAdapter = MyNoteAdapter(this.context!!, lastNotes)
                                                myList.adapter = myAdapter

                                                //finish()
                                                //startActivity(getIntent())
                                            } else
                                                querySearch("%")
                                        }
                                        dialogBuilder.show()
                                    }

                                } else {
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