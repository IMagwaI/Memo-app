package com.example.myapplication.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.fragment_add_voice.*
import java.util.*
import kotlin.collections.ArrayList


class AddVoiceFragment : Fragment() {
    private val RQ_SPEECH_REC=102
    var id:Int?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        record.setOnClickListener{
            askSpeechInput()
        }

        save.setOnClickListener {
            addNote()
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
            Toast.makeText(this.requireContext(),"Speech recognitionis not available",Toast.LENGTH_SHORT).show()
        }else{
           val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something!")
            startActivityForResult(i,RQ_SPEECH_REC)

        }
    }

    fun addNote(){
        var title:String?=title.text.toString()
        var note:String?=textDisplay.text.toString()
        val values= ContentValues()
        values.put("title",title)
        values.put("description",note)
        val dbManager= DbManager(this.requireActivity())
        if(id!=0&&id!=null){
            val selectionArgs= arrayOf(id.toString())
            val id=dbManager.update(values,"ID=?",selectionArgs)
            if(id>0)
                Toast.makeText(this.requireContext(),"database updated",Toast.LENGTH_LONG).show()

        }else {
            val id = dbManager.insertNote(values)
            if(id>0)
                Toast.makeText(this.requireContext(),"added to database",Toast.LENGTH_LONG).show()

        }


    }
}