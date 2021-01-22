package com.example.note.firebase.fragments

import android.os.Build
import android.os.Bundle
import android.util.Base64.encodeToString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_firebase_save.*
import java.util.*


class SaveFragment : Fragment(){
    private lateinit var gsclient: GoogleSignInClient
    private var saveListNotes = ArrayList<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_firebase_save, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val string:String="hey "+firebaseAuth.currentUser!!.displayName
        nameDisplay.text = string

        // Write a message to the database
        val database = FirebaseDatabase.getInstance().reference

        saveFirebase.setOnClickListener {
            querySearch("%")

            /**
             * store by ID_Title_DescriptionLength
             */
            for (l in saveListNotes)
                database.child(firebaseAuth.uid.toString())
                    .child(l.date.toString() + l.title.toString() + l.description?.length.toString())
                    .setValue(l)
        }


        super.onViewCreated(view, savedInstanceState)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun querySearch(search: String) {
        var dbManager = DbManager(this.requireContext())
        val projections = arrayOf("ID", "title", "description", "date", "img", "reminderdate")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "ID like ?", selectionArgs, "ID")
        if (cursor.moveToFirst()) {
            saveListNotes.clear()
            do {
                val id = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val img = cursor.getBlob(cursor.getColumnIndex("img"))
                val reminderDate=cursor.getString(cursor.getColumnIndex("reminderdate"))
                val date = cursor.getString(3)
                try {
                    val base64Encoded = Base64.getEncoder().encodeToString(img)
                    saveListNotes.add(Note(id, title, description, date, base64Encoded, reminderDate))
                }catch (e: Exception){
                    saveListNotes.add(Note(id, title, description, date, reminderDate))
                }

            } while (cursor.moveToNext())
        }
        println("----------------------------------------")
        println(saveListNotes.get(0).img)
        println(saveListNotes.get(1).img)

        dbManager.sqlDB!!.close()
    }
}