
package com.example.mldcchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

private lateinit var mAuth: FirebaseAuth

private lateinit var mVerifybtn: Button
private lateinit var mOtptextview: EditText
private lateinit var mProgress: ProgressBar
private lateinit var mFeedback: TextView





class VerifyOtp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        mAuth = FirebaseAuth.getInstance()

        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        mVerifybtn = findViewById(R.id.verify_btn)
        mOtptextview = findViewById(R.id.otp_text_view)
        mProgress = findViewById(R.id.otp_progress_bar)
        mFeedback = findViewById(R.id.otp_form_feedback)

        mVerifybtn.setOnClickListener{
            var code = mOtptextview.text.toString()

            if (code.isEmpty()){
                mFeedback.visibility = View.VISIBLE
                mFeedback.setText(R.string.otp_error_message)
            }else{
                mProgress.visibility = View.VISIBLE
                mVerifybtn.isEnabled = false
                val credential = PhoneAuthProvider.getCredential(storedVerificationId.toString(), code)
                signInWithPhoneAuthCredential(credential)

            }
        }


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, SignUpActivity::class.java))
                    finish()

                    val user = task.result?.user
                    // ...
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        mFeedback.visibility = View.VISIBLE
                        mFeedback.setText(R.string.otp_error)
                    }
                }
                mProgress.visibility = View.INVISIBLE
                mVerifybtn.isEnabled = true
            }
    }






}