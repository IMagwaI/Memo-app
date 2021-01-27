package com.example.myapplication.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.BaseActivity
import com.example.myapplication.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsclient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth= FirebaseAuth.getInstance()
        gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("34402196657-onggn4hj92h9sur9eb62f1p5sprqg43c.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsclient= GoogleSignIn.getClient(this,gso)
        val gsaccount:GoogleSignInAccount? =GoogleSignIn.getLastSignedInAccount(this)
        if(gsaccount!=null ||firebaseAuth.currentUser!=null){
            Toast.makeText(this,"User is logged in already",Toast.LENGTH_SHORT).show()
            val intentSave = Intent(this, OnlineDBActivity::class.java)
            startActivity(intentSave)

        }
        login.setOnClickListener {
            val signInIntent: Intent = gsclient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                val authCredential:AuthCredential= GoogleAuthProvider.getCredential(account?.idToken,null)
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener{
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Google account connected", Toast.LENGTH_LONG).show()
                        val intentSave = Intent(this, OnlineDBActivity::class.java)
                        startActivity(intentSave)
                    } else {
                        Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

}