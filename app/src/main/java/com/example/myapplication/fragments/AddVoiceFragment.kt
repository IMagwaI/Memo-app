package com.example.myapplication.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.localdb.DbManager
import com.example.myapplication.notif.NotificationSchedule
import kotlinx.android.synthetic.main.fragment_add_draw.*
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_add_voice.*
import kotlinx.android.synthetic.main.fragment_add_voice.save
import kotlinx.android.synthetic.main.fragment_add_voice.switch1
import kotlinx.android.synthetic.main.fragment_add_voice.textReminder
import kotlinx.android.synthetic.main.noteticket.*
import java.util.*


class AddVoiceFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val RQ_SPEECH_REC=102
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
        return inflater.inflate(R.layout.fragment_add_voice, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        record.setOnClickListener{
            askSpeechInput()
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
                //DeleteNotification
                textReminder.visibility=View.INVISIBLE
                reminderDate=null
            }
        }
        save.setOnClickListener {
            val editText = EditText(this.requireContext());
            val dialog: AlertDialog = AlertDialog.Builder(this.requireContext())
                .setTitle("Title")
                .setView(editText)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    val editTextInput: String = editText.getText().toString()
                    if (reminderDate==null || reminderDate!!.timeInMillis > Calendar.getInstance().timeInMillis) {
                        val intent = Intent(this.context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        addNote(editTextInput)
                    }
                    else
                    {Toast.makeText(this.context!!, "Date invalide", Toast.LENGTH_LONG).show()}

                })
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RQ_SPEECH_REC&&resultCode== Activity.RESULT_OK){
            val result:ArrayList<String>? =data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            textDisplay.setText(result?.get(0).toString())
        }

    }
    private fun askSpeechInput(){
        if(!SpeechRecognizer.isRecognitionAvailable(this.requireContext())){
            Toast.makeText(
                this.requireContext(),
                "Speech recognitionis not available",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!")
            startActivityForResult(i, RQ_SPEECH_REC)

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, Year: Int, Month: Int, dayOfMonth: Int) {
        day = dayOfMonth
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
        reminderDate= Calendar.getInstance()
        hour = hourOfDay
        minute = Minute
        textReminder.text = " "+year + "/" +(month+1) + "/" + day + "at "  + hour + ":"  + minute
        reminderDate!!.set(Calendar.MINUTE, minute)
        reminderDate!!.set(Calendar.HOUR, hour)
        reminderDate!!.set(Calendar.MONTH, month)
        reminderDate!!.set(Calendar.DAY_OF_MONTH, day)
        reminderDate!!.set(Calendar.YEAR,year)
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
    fun addNote(title:String){
        val note:String=textDisplay.text.toString()
        var id: UUID?=null
        val values= ContentValues()
        values.put("title", title)
        values.put("description", note)
        if(reminderDate!=null) {
            values.put("reminderdate", reminderDate!!.time.toString())
            var delay: Long = Math.abs(System.currentTimeMillis() - reminderDate!!.time.time)
            println("this is delay" + delay)
            id = scheduleNotification(delay / 1000, title!!, note!!)
        }else values.put("reminderdate", "null")

        values.put("notifid",id.toString())
        val dbManager= DbManager(this.requireActivity())
        if(idNote!=0&&idNote!=null){
            val selectionArgs= arrayOf(idNote.toString())
            val id=dbManager.update(values, "ID=?", selectionArgs)
            if(id>0)
                Toast.makeText(this.requireContext(), "database updated", Toast.LENGTH_LONG).show()

        }else {
            val id = dbManager.insertNote(values)
            if(id>0)
                Toast.makeText(this.requireContext(), "added to database", Toast.LENGTH_LONG).show()

        }


    }
}