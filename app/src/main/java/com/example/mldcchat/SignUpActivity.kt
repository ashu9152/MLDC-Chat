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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.user_row_new_message.*


class SignUpActivity : AppCompatActivity() {

    lateinit var btnNext : Button
    lateinit var userImgView : ShapeableImageView
    lateinit var textEt : EditText
    lateinit var errortext : TextView
    lateinit var auth: FirebaseAuth
    lateinit var downloadUrl : String
    lateinit var mdatabase : FirebaseDatabase
    lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        btnNext = findViewById(R.id.nextBtn)
        userImgView = findViewById(R.id.userImgView)
        textEt = findViewById(R.id.nameEt)
        errortext = findViewById(R.id.error_text)
        auth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance()
        database = FirebaseDatabase.getInstance().reference





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
                val users = User(textET, downloadUrl, auth.uid!!)
                //this will create values in Database
                Log.d("SavedUser", "Add User Sucessfully")
                database.child("user").child(auth.uid!!).setValue(users).addOnSuccessListener {
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
        val ref = FirebaseStorage.getInstance().reference.child("images/${auth.uid.toString()}")
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
                saveUserToFireBaseDataBase(downloadUrl)
            }
        }
    }
    private fun saveUserToFireBaseDataBase(profileImageUrl: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${auth.uid.toString()}")

        val user = User(auth.uid.toString(), nameEt.text.toString(),profileImageUrl)
        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Finally we saved the user to Database")
                }
    }




}



