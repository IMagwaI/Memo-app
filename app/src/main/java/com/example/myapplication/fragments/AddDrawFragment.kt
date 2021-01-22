package com.example.myapplication.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.extraBitmap
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.fragment_add_draw.*
import java.io.ByteArrayOutputStream
import androidx.fragment.app.FragmentActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.drawColor
import com.example.myapplication.notif.NotificationSchedule
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.android.synthetic.main.activity_add.*
import java.util.*

class AddDrawFragment : Fragment() , DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener{
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
    lateinit var reminderDate: Date
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_draw, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        val colors = resources.getIntArray(R.array.rainbow)

        color.setOnClickListener {
            ColorSheet().cornerRadius(8)
                .colorPicker(
                    colors = colors,
                    listener = { color -> drawColor=color
                    })
                .show((activity as FragmentActivity).supportFragmentManager)

        /* (activity as FragmentActivity).supportFragmentManager.let {
                OptionsBottomSheetFragment.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }*/
        }

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
                textReminder.visibility=View.INVISIBLE
                reminderDate=Date(0)

            }
        }
        save.setOnClickListener {
            addNote()
        }

        super.onViewCreated(view, savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = day
        myYear = year
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
        textReminder.text = " "+myYear + "/" + myMonth + "/" + myDay + "at "  + myHour + ":"  + myMinute
        reminderDate = Date(myYear, myMonth, myDay, myHour, myMinute)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun scheduleNotification(timeDelay: Long, tag: String, body: String) {

        val data = Data.Builder().putString("body", body)

        val work = OneTimeWorkRequestBuilder<NotificationSchedule>()
            .setInitialDelay(timeDelay, java.util.concurrent.TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder().setTriggerContentMaxDelay(1,
                java.util.concurrent.TimeUnit.SECONDS
            ).build()) // API Level 24
            .setInputData(data.build())
            .addTag(tag)
            .build()

        WorkManager.getInstance().enqueue(work)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addNote() {
        val title: String = "Draw"
        val note: String = "This is a drawing note, press edit button to display it"
        val bitmap: Bitmap = extraBitmap

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val img: ByteArray = bos.toByteArray()

        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        values.put("img", img)
        try {
            values.put("reminderdate", reminderDate.toString())
        }catch (e:Exception) {
            values.put("reminderdate", "null")
        }
        val dbManager = DbManager(this.requireContext())
        val id = dbManager.insertNote(values)

        dbManager.sqlDB!!.close()
        if (reminderDate.toString()!="null"){
            var delay:Long= Math.abs(System.currentTimeMillis() - reminderDate!!.time)
            println("this is delay"+delay)
            scheduleNotification(delay/1000,title!!,note!!)
        }


    }

}