package com.example.myapplication.fragments



import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
import java.lang.Math.abs
import java.util.*


var idNote: Int? = null
var titleNote:String?=""
var descriptionNote:String?=""
var passwordNote:String?=""
@RequiresApi(Build.VERSION_CODES.N)
var reminderDate :Calendar?=null

class AddTextNoteFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0

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
        print("print this HAHA $reminderDate")
        switch1.isChecked = reminderDate!=null
        switch2.isChecked = passwordNote!=""
        switch1?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //DatePicker
                //TimePicker
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(this.context!!, this, year, month, day)
                dpd.datePicker.minDate = c.timeInMillis;
                dpd.show()
                textReminder.visibility=View.VISIBLE
            } else {
                //DeleteNotification
                if (idNote != 0 && idNote != null) {
                    val dbManager = DbManager(this.context!!)
                    val selectionArgs = arrayOf(idNote.toString())
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
                textReminder.visibility=View.INVISIBLE

                reminderDate=null
            }
        }
        switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val password = EditText(this.requireContext())
                val dialog: AlertDialog = AlertDialog.Builder(this.requireContext())
                    .setTitle("Password")
                    .setView(password)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        val editTextPassword: String = password.getText().toString()
                        textPassword.setText(editTextPassword)
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                dialog.show()}
            else{
                textPassword.setText("")
                }
        }
        saveButton.setOnClickListener {
            val editText = EditText(this.requireContext())
            val dialog: AlertDialog = AlertDialog.Builder(this.requireContext())
                .setTitle("Title")
                .setView(editText)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    val editTextInput: String = editText.getText().toString()
                    if (reminderDate==null || reminderDate!!.timeInMillis > Calendar.getInstance().timeInMillis) {
                        val intent = Intent(this.context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        if(switch2.isChecked){
                            addNote(editTextInput,textPassword.getText().toString())
                        }else{
                            addNote(editTextInput,"")

                        }

                    }
                    else
                    {Toast.makeText(this.context!!, "Date invalide", Toast.LENGTH_LONG).show()}

                })
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }

        //check edit
        if (idNote != 0 && idNote != null) {
            //val title = titleNote
            val description = descriptionNote
            val password = passwordNote
            multiLineText.setText(description)
            textPassword.setText(password)
            if(reminderDate.toString()!="null")
            textReminder.text = reminderDate!!.time.toString()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, Year: Int, Month: Int, dayOf: Int) {
        day = dayOf
        year = Year
        month = Month
        val timePickerDialog = TimePickerDialog(
            this.context, this, hour, minute,
            DateFormat.is24HourFormat(this.context)
        )
        timePickerDialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, Minute: Int) {
        reminderDate=Calendar.getInstance()
        hour = hourOfDay
        minute = Minute
        textReminder.text = " "+year + "/" + month+1 + "/" + day + " at "  + hour + ":"  + minute
        reminderDate!!.set(Calendar.MINUTE, minute);
        reminderDate!!.set(Calendar.HOUR_OF_DAY, hour);
        reminderDate!!.set(Calendar.MONTH, month);
        reminderDate!!.set(Calendar.DAY_OF_MONTH, day);
        reminderDate!!.set(Calendar.YEAR, year);
        if (reminderDate!!.timeInMillis <= Calendar.getInstance().timeInMillis)
        {Toast.makeText(this.context!!, "Invalid Time", Toast.LENGTH_LONG).show()}
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun scheduleNotification(timeDelay: Long, tag: String, body: String):UUID {

        val data = Data.Builder().putString("body", body)

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
        return work.id
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addNote(title:String?, password:String?) {
        val note: String? = multiLineText.text.toString()
        var id: UUID?=null
        val values = ContentValues()
        values.put("title", title)
        values.put("description", note)
        values.put("password",password)
        if(reminderDate!=null) {
            values.put("reminderdate", reminderDate!!.time.toString())
            var delay: Long = abs(System.currentTimeMillis() - reminderDate!!.time.time)
            println("this is delay" + delay)
            id = scheduleNotification(delay / 1000, title!!, note!!)
        }else values.put("reminderdate", "null")

        values.put("notifid", id.toString())
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

    }

}