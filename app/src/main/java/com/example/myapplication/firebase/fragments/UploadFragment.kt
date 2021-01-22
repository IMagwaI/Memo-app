package com.example.note.firebase.fragments

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.localdb.DbManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_firebase_upload.*
import java.util.ArrayList

class UploadFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_firebase_upload, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)


        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val rootRef = FirebaseDatabase.getInstance().reference
        val usersRef = rootRef.child(firebaseAuth.uid.toString())
        upload.setOnClickListener {
            val valueEventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val list: MutableList<String?> = ArrayList()
                    for (ds in dataSnapshot.children) {
                        val uid = ds.key
                        list.add(uid)
                        println(uid)
                        addNote(ds.child("title").value.toString(),ds.child("description").value.toString())
                    }

                    //Do what you need to do with your list
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError.message) //Don't ignore errors!
                }
            }
            usersRef.addListenerForSingleValueEvent(valueEventListener)

        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun addNote(title:String,note:String){
        val values= ContentValues()
        values.put("title",title)
        values.put("description",note)
        val dbManager= DbManager(this.requireActivity())

        val id = dbManager.insertNote(values)
        if(id>0)
            Toast.makeText(this.requireContext(),"added to database", Toast.LENGTH_SHORT).show()

    }


}


