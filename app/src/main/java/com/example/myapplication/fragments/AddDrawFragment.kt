package com.example.myapplication.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.*
import com.example.myapplication.localdb.DbManager
import com.example.myapplication.notif.NotificationSchedule
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.android.synthetic.main.fragment_add_draw.*
import kotlinx.android.synthetic.main.fragment_add_draw.save
import kotlinx.android.synthetic.main.fragment_add_draw.switch1
import kotlinx.android.synthetic.main.fragment_add_draw.switch2
import kotlinx.android.synthetic.main.fragment_add_draw.textPassword
import kotlinx.android.synthetic.main.fragment_add_draw.textReminder
import kotlinx.android.synthetic.main.fragment_add_voice.*
import java.io.ByteArrayOutputStream
import java.util.*


class AddDrawFragment : Fragment() , DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener{
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    @RequiresApi(Build.VERSION_CODES.N)
    var reminderDate :Calendar?=null
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
                    listener = { color ->
                        drawColor = color
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
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(this.context!!,this, year, month, day)
                dpd.datePicker.minDate = c.timeInMillis;
                dpd.show()
                textReminder.visibility=View.VISIBLE
            } else {
                textReminder.visibility=View.INVISIBLE
                reminderDate=null

            }
        }
        switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val password = EditText(this.context)
                val dialog: AlertDialog = AlertDialog.Builder(this.context!!)
                    .setTitle("Password")
                    .setView(password)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        val editTextPassword: String = password.getText().toString()
                        textPassword.setText(editTextPassword)
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                dialog.show()} else{
                textPassword.setText("")
            }
        }
        save.setOnClickListener {
            if (reminderDate==null || reminderDate!!.timeInMillis > Calendar.getInstance().timeInMillis) {
                val intent = Intent(this.context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                if(switch2.isChecked){
                    addNote(textPassword.getText().toString())
                }else{
                    addNote("")

                }
            }
            else
            {
                Toast.makeText(this.context!!, "Date invalide", Toast.LENGTH_LONG).show()}
        }

        super.onViewCreated(view, savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, Year: Int, Month: Int, dayOf: Int) {
        day = dayOf
        year = Year
        month = Month
        val timePickerDialog = TimePickerDialog(this.context, this, hour, minute,
            DateFormat.is24HourFormat(this.context))
        timePickerDialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, Minute: Int) {
        reminderDate=Calendar.getInstance()
        hour = hourOfDay
        minute = Minute
        reminderDate!!.set(Calendar.MINUTE, minute);
        reminderDate!!.set(Calendar.HOUR_OF_DAY, hour);
        reminderDate!!.set(Calendar.MONTH, month);
        reminderDate!!.set(Calendar.DAY_OF_MONTH, day);
        reminderDate!!.set(Calendar.YEAR,year);
        textReminder.text = " "+year + "/" + month+1 + "/" + day + " at "  + hour + ":"  + minute
        if (reminderDate!!.timeInMillis <= Calendar.getInstance().timeInMillis)
        {Toast.makeText(this.context!!, "Invalid Time", Toast.LENGTH_LONG).show()}
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun scheduleNotification(timeDelay: Long, tag: String, body: String):UUID {

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
        return work.id
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addNote(password:String?) {
        val title: String = "Draw ("+extraBitmap.generationId.toString()+")"
        val note: String = "This is a drawing note, press the note to display it"
        val bitmap: Bitmap = extraBitmap
        var notifId: UUID?=null

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val img: ByteArray = bos.toByteArray()

        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        values.put("img", img)
        values.put("isNoteDeleted","0")
        values.put("password",password)
        /*try {
            values.put("reminderdate", reminderDate.toString())
        }catch (e: Exception) {
            values.put("reminderdate", "null")
        }*/
        if(reminderDate!=null) {
            values.put("reminderdate", reminderDate!!.time.toString())
            var delay: Long = Math.abs(System.currentTimeMillis() - reminderDate!!.time.time)
            println("this is delay" + delay)
            notifId = scheduleNotification(delay / 1000, title!!, note!!)
        }else values.put("reminderdate", "null")

        values.put("notifid",notifId.toString())
        val dbManager = DbManager(this.requireContext())
        val id = dbManager.insertNote(values)

        dbManager.sqlDB!!.close()



    }

}