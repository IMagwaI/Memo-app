package com.example.myapplication


import android.content.DialogInterface
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.localdb.DbManager
import com.github.tibolte.agendacalendarview.CalendarPickerController
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent
import com.github.tibolte.agendacalendarview.models.CalendarEvent
import com.github.tibolte.agendacalendarview.models.DayItem
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*
import kotlin.collections.ArrayList


class CalendarActivity : BaseActivity(), CalendarPickerController {
    val eventList: List<CalendarEvent> = ArrayList()
    var descrip = ArrayList<String>()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        title = "Calendar"
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()

        minDate.add(Calendar.MONTH, -2)
        minDate[Calendar.DAY_OF_MONTH] = 1
        maxDate.add(Calendar.YEAR, 1)
        mockList(eventList as MutableList<CalendarEvent>)

        agenda_calendar_view.init(eventList, minDate, maxDate, Locale.getDefault(), this)
    }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun mockList(eventList: MutableList<CalendarEvent>) {
            var dbManager = DbManager(this)
            val projections = arrayOf("ID", "title", "description", "reminderdate", "isNoteDeleted")
            val selectionArgs = arrayOf("null","0")
            var cursor = dbManager.query(projections, "reminderdate != ? AND isNoteDeleted=?",selectionArgs,"reminderdate ")

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("ID"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val reminderdate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                    println("data = " + id.toString() + " " + title + " " + reminderdate)
                    val cal = Calendar.getInstance()
                    val sdf = SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH)
                    cal.time = sdf.parse(reminderdate)
                    val event = BaseCalendarEvent(id.toLong(), Color.parseColor("#cfcfc4"), title,description, "",cal.getTimeInMillis(),cal.getTimeInMillis(), 1, null)
                    // BaseCalendarEvent(
                    //                    title, description, "",
                    //                    Color.parseColor("#cfcfc4"), cal, cal, true
                    //                )
                    print("DESCRIPTION"+description)
                    descrip.add(description)
                    eventList.add(event)


                } while (cursor.moveToNext())
            }
            cursor.close()
        }

        override fun onDaySelected(dayItem: DayItem?) {
            Toast.makeText(getApplicationContext(),"DAY SELECTED", Toast.LENGTH_SHORT);
        }

        override fun onEventSelected(event: CalendarEvent?) {
            var description:String?=null
            Toast.makeText(getApplicationContext(),"EVENT SELECTED",Toast.LENGTH_SHORT);
            for(i in eventList.indices){
                if(eventList[i].id==event!!.id)
                    description=descrip[i]
            }
            val alertDialog = AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_menu_agenda)
                //set title
                .setTitle(event!!.title)
                //set message
                .setMessage(description)
                //set positive button
                .setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, i ->
                    //set what would happen when positive button is clicked
                    finish()
                })
                .show()
        }

        override fun onScrollToDate(calendar: Calendar?) {
            Toast.makeText(getApplicationContext(),"SCROLL DATE",Toast.LENGTH_SHORT);
        }
}
