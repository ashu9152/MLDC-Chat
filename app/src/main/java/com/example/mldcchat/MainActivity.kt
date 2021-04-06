package com.example.mldcchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

     lateinit var mAuth: FirebaseAuth
     lateinit var signoutbtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var toolBar1: Toolbar
        toolBar1 = findViewById(R.id.toolbar1)
        setSupportActionBar(toolBar1)

          mAuth = FirebaseAuth.getInstance()
         signoutbtn = findViewById(R.id.signOut)

        var currentUser = mAuth.currentUser
        if(currentUser==null){
            startActivity(Intent(applicationContext, PhoneNumber::class.java))
              finish()
        }

         signoutbtn.setOnClickListener {
            mAuth.signOut()
           startActivity(Intent(this, PhoneNumber::class.java))
             finish()
       }
    }

}