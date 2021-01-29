package com.example.myapplication.firebase.fragments

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_firebase_upload.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.util.Base64


class UploadFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_firebase_upload, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)


        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val rootRef = FirebaseDatabase.getInstance().reference
        val usersRef = rootRef.child(firebaseAuth.uid.toString())
        try{
            upload.setOnClickListener {
                querySearch("%")
                val valueEventListener: ValueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val list: MutableList<String?> = ArrayList()
                        for (ds in dataSnapshot.children) {
                            val uid = ds.key
                            list.add(uid)
                            println(uid)
                            addNote(
                                ds.child("title").value.toString(),
                                ds.child("description").value.toString(),
                                ds.child("date").value.toString(),
                                ds.child("imgS").value.toString(),
                                ds.child("reminderdate").value.toString()
                            )
                        }

                        //Do what you need to do with your list
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println(databaseError.message) //Don't ignore errors!
                    }
                }
                usersRef.addListenerForSingleValueEvent(valueEventListener)

            }
        }catch (e: Error){
            Toast.makeText(this.requireContext(),"Service failed, this feature required android api +26",Toast.LENGTH_LONG).show()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote(title: String, note: String, date: String, imgS: String, reminderdate: String){
        var check=true
        for(l in savedListNotes){
            if (l.title==title&&l.description==note&&l.date==date&&l.imgS==imgS&&l.reminderdate==reminderdate)
                check=false
        }
        try {
            if (check) {
                val values = ContentValues()
                values.put("title", title)
                values.put("description", note)
                values.put("reminderdate", reminderdate)
                val decodedByte: ByteArray = Base64.getDecoder().decode(imgS)
                values.put("img", decodedByte)
                values.put("date", date)


                val dbManager = DbManager(this.requireActivity())

                val id = dbManager.insertNote(values)
                if (id > 0)
                    Toast.makeText(this.requireContext(), "added to database", Toast.LENGTH_SHORT)
                        .show()
            }
        }catch (e: Error){
            Toast.makeText(this.requireContext(),"Service failed, this feature required android api +26",Toast.LENGTH_LONG).show()
        }
    }



    private var savedListNotes = ArrayList<Note>()
    @RequiresApi(Build.VERSION_CODES.O)
    fun querySearch(search: String) {
        var dbManager = DbManager(this.requireContext())
        val projections = arrayOf("ID", "title", "description", "date", "img", "reminderdate")
        val selectionArgs = arrayOf(search)
        var cursor = dbManager.query(projections, "ID like ?", selectionArgs, "ID")
        try {
            if (cursor.moveToFirst()) {
                savedListNotes.clear()
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("ID"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val img = cursor.getBlob(cursor.getColumnIndex("img"))
                    val reminderDate = cursor.getString(cursor.getColumnIndex("reminderdate"))
                    val date = cursor.getString(3)
                    try {
                        val base64Encoded = Base64.getEncoder().encodeToString(img)
                        savedListNotes.add(
                            Note(
                                id,
                                title,
                                description,
                                date,
                                base64Encoded,
                                reminderDate
                            )
                        )
                    } catch (e: Exception) {
                        savedListNotes.add(Note(id, title, description, date, reminderDate))

                    }

                } while (cursor.moveToNext())
            }
        }catch (e: Error){
            Toast.makeText(this.requireContext(),"Service failed, this feature required android api +26",Toast.LENGTH_LONG).show()
        }


        dbManager.sqlDB!!.close()
    }
}


