package com.example.myapplication


import android.graphics.Color
import android.graphics.Color.blue
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*
import kotlin.collections.ArrayList


class CalendarActivity : BaseActivity(){
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val events: MutableList<EventDay> = ArrayList()



       var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "reminderdate")
        val selectionArgs = arrayOf("null")
        var cursor = dbManager.query(projections, "reminderdate != ?", selectionArgs, "ID")
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val reminderdate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                if(reminderdate!="null") {
                    val cal = Calendar.getInstance()
                    val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                    cal.time = sdf.parse(reminderdate)
                    println("data = " + id.toString() + " " + title + " "+cal+reminderdate)
                    events.add(EventDay(cal, R.drawable.ic_notes, Color.parseColor("#228B22")))
                }

            } while (cursor.moveToNext())
        }
        cursor.close()






       val calendarView: CalendarView = findViewById<View>(R.id.calendarView) as CalendarView
       calendarView.setHeaderColor(R.color.orange)
       calendarView.setHeaderLabelColor(R.color.white);
        calendarView.setEvents(events)
    }
}