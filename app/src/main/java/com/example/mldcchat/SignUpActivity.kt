package com.example.mldcchat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask


class SignUpActivity : AppCompatActivity() {

    lateinit var btnNext : Button
    lateinit var userImgView : ShapeableImageView
    lateinit var textEt : EditText
    lateinit var errortext : TextView
    lateinit var auth: FirebaseAuth
    lateinit var downloadUrl : String
    lateinit var database : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        btnNext = findViewById(R.id.nextBtn)
        userImgView = findViewById(R.id.userImgView)
        textEt = findViewById(R.id.nameEt)
        errortext = findViewById(R.id.error_text)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()


        userImgView.setOnClickListener {
            openGallery()
        }

        btnNext.setOnClickListener {
            val textET = textEt.text.toString()
            if (!::downloadUrl.isInitialized) {
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            }else if(textET.isEmpty()){
                errortext.visibility = View.VISIBLE
            } else{
                val user = User(textET, downloadUrl, auth.uid!!)
                //this will create values in firestrore
                database.collection("user").document(auth.uid!!).set(user).addOnSuccessListener {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {

                }
            }
        }
    }

    override fun onBackPressed() {

    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {
            intent?.data?.let { image ->
                userImgView.setImageURI(image)
                uploadImage(image)
            }
        }
    }

    private fun uploadImage(image: Uri) {
        val ref = FirebaseStorage.getInstance().reference.child("upload/${auth.uid.toString()}")
        val uploadTask = ref.putFile(image)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->
        if (!task.isSuccessful){
            Log.e("Error Uploading", task.exception.toString())
        }
            return@Continuation ref.downloadUrl

        }).addOnCompleteListener{task ->
            if (task.isSuccessful){
                Log.e("Done Uploading", task.result.toString())
                downloadUrl =task.result.toString()
            }

        }
    }
}