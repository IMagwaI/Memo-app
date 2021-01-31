package com.example.myapplication.fragments



import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.localdb.DbManager
import com.example.myapplication.notif.NotificationSchedule
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_add_note.switch1
import kotlinx.android.synthetic.main.fragment_add_note.textReminder
import java.lang.Math.abs
import java.time.LocalDateTime
import java.util.*


var idNote: Int? = null
var titleNote:String?=""
var descriptionNote:String?=""

class AddTextNoteFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0
    var reminderDate: Date?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        switch1?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //DatePicker
                //TimePicker
                val calendar: Calendar = Calendar.getInstance()
                day = calendar.get(Calendar.DAY_OF_MONTH)
                month = calendar.get(Calendar.MONTH)
                year = calendar.get(Calendar.YEAR)
                val datePickerDialog =
                    DatePickerDialog(this.context!!, this, year, month,day)
                datePickerDialog.show()
                textReminder.visibility=View.VISIBLE
            } else {
                //DeleteNotification
                textReminder.visibility=View.VISIBLE
                reminderDate=null
            }
        }
        saveButton.setOnClickListener {
            addNote()
            val intent = Intent(this.context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }

        //check edit
        if (idNote != 0 && idNote != null) {
            val title = titleNote
            titleText.setText(title)
            val description = descriptionNote
            multiLineText.setText(description)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = day
        myYear = year-1900
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this.context, this, hour, minute,
            DateFormat.is24HourFormat(this.context))
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        textReminder.text = " "+myYear + "/" + myMonth + "/" + myDay + " at "  + myHour + ":"  + myMinute
        reminderDate = Date(myYear, myMonth, myDay, myHour, myMinute)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun scheduleNotification(timeDelay: Long, tag: String, body: String) {

        val data = Data.Builder().putString("body", body)

        val work = OneTimeWorkRequestBuilder<NotificationSchedule>()
            .setInitialDelay(timeDelay, java.util.concurrent.TimeUnit.SECONDS)
            .setConstraints(Constraints.Builder().setTriggerContentMaxDelay(1,
                java.util.concurrent.TimeUnit.SECONDS
            ).build()) // API Level 24
            .setInputData(data.build())
            .addTag(tag)
            .build()

        WorkManager.getInstance().enqueue(work)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote() {
        val title: String? = titleText.text.toString()
        val note: String? = multiLineText.text.toString()

        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        values.put("reminderdate", reminderDate.toString())
        val dbManager = DbManager(this.requireActivity())
        if (idNote != 0 && idNote != null) {
            val selectionArgs = arrayOf(idNote.toString())
            val id = dbManager.update(values, "ID=?", selectionArgs)
            if (id > 0)
                Toast.makeText(this.requireContext(), "database updated", Toast.LENGTH_LONG).show()

        } else {
            val id = dbManager.insertNote(values)
            if (id > 0)
                Toast.makeText(this.requireContext(), "added to database", Toast.LENGTH_LONG).show()

        }
        if (reminderDate.toString()!="null"){
            var delay:Long= abs(System.currentTimeMillis()- reminderDate!!.time)
            println("this is delay"+delay)
            scheduleNotification(delay/1000,title!!,note!!)
        }
    }

}