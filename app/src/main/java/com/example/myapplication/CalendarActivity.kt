package com.example.myapplication


import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import com.riontech.calendar.CustomCalendar
import com.riontech.calendar.*
import com.riontech.calendar.dao.EventData
import com.riontech.calendar.dao.dataAboutDate
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*
import kotlin.collections.ArrayList


class CalendarActivity : BaseActivity(){
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        title = "Calendar"
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        //val events: MutableList<EventDay> = ArrayList()
//        val listnotes: MutableList<Note> = ArrayList()
        val arr = ArrayList<String>()




        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "title", "description", "reminderdate","date")
        val selectionArgs = arrayOf("null")
        var cursor = dbManager.query(projections, "reminderdate != ?", selectionArgs, "reminderdate ")
        var list=ArrayList<EventData>()
        var listdata:ArrayList<dataAboutDate>
        var datebefore=""
        var eventcount=0
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val reminderdate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                val subdate = cursor.getString(cursor.getColumnIndex("date"))
                println("data = " + id.toString() + " " + title + " "  + reminderdate)
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.ENGLISH)
                cal.time = sdf.parse(reminderdate)
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                val date = formatter.format(cal.time)
                println("DATE"+date)
                if(eventcount==0) {
                    datebefore = date
                }
                else if(date!=datebefore){
                    eventcount=0
                    list= ArrayList()
                }
                var data= dataAboutDate()
                var eventData=EventData()
                eventData.section="Note"
                data.remarks=""
                data.subject=description
                data.submissionDate=subdate
                data.title=title
                listdata=ArrayList()
                listdata.add(data)
                eventData.data=listdata
                list.add(eventData)
                eventcount++
                customCalendar.addAnEvent(date, eventcount, list)
                datebefore=date
                println("data = " + id.toString() + " " + title + " " +reminderdate+" /"+eventcount+"size"+list.size)

            } while (cursor.moveToNext())
        }
        cursor.close()





        val customCalendar = findViewById<View>(R.id.customCalendar) as CustomCalendar

        //val eventCount = 3



        /*calendarView.setHeaderColor(R.color.orange)
       calendarView.setHeaderLabelColor(R.color.white);
        calendarView.setEvents(events)
        calendarView.setOnDayLongClickListener(object : OnDayLongClickListener {
            override fun onDayLongClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                print(clickedDayCalendar)
            }
        })
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                print(clickedDayCalendar)
                for (i in 0..listnotes.size - 1) {
                    print(listnotes[i].reminderdate)
                    print(clickedDayCalendar.time.toString())
                    if (listnotes[i].reminderdate == clickedDayCalendar.time.toString()) {
                        print("hahahahah")
                        note.text = listnotes[i].description
                    }
                }

                // Toast.makeText(this.context!!,"bonjour",1)
            }
        })
    }*/
    }
}