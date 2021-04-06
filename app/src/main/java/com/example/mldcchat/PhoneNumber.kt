package com.example.mldcchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private lateinit var mAuth: FirebaseAuth

private  lateinit var mcountrycode:EditText
private lateinit var mphonenumber:EditText

private lateinit var mgeneratebtn:Button
private lateinit var mprogressbar:ProgressBar
private lateinit var mloginfeedback:TextView

lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
lateinit var storedVerificationId:String



class PhoneNumber : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)



        mAuth = FirebaseAuth.getInstance()

        mcountrycode = findViewById(R.id.country_code_text)
        mphonenumber = findViewById(R.id.phone_number_text)
        mgeneratebtn = findViewById(R.id.generate_btn)
        mprogressbar = findViewById(R.id.login_progress_bar)
        mloginfeedback = findViewById(R.id.login_form_feedback)

        var currentUser = mAuth.currentUser
        if(currentUser != null){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        mgeneratebtn.setOnClickListener {
           login()
        }

        callbacks = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(Credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, SignUpActivity::class.java ))
                finish()
            }

            override fun onVerificationFailed(p0: FirebaseException) {

                mloginfeedback.setText(R.string.error_with_number)
                mloginfeedback.visibility = View.VISIBLE
                mprogressbar.visibility = View.INVISIBLE
                mgeneratebtn.isEnabled = true

            }

            override fun onCodeSent(VereficationID: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(VereficationID, p1)

                storedVerificationId = VereficationID

                var intent = Intent(applicationContext, VerifyOtp::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId )
                startActivity(intent)
                finish()
            }
        }
    }

    private fun login() {
        val countrycode = mcountrycode.text.toString()
        val phonenumber = mphonenumber.text.toString()

        var number =   "+" + countrycode + phonenumber

        if (countrycode.isEmpty() || phonenumber.isEmpty()) {
            mloginfeedback.setText(R.string.error_message)
            mloginfeedback.visibility = View.VISIBLE
        }
        else if(phonenumber.length<10 || phonenumber.length>10){
            mloginfeedback.setText(R.string.phone_greater)
            mloginfeedback.visibility = View.VISIBLE
        }
        else {
            mprogressbar.visibility = View.VISIBLE
            mgeneratebtn.isEnabled = false

            val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(number)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}




