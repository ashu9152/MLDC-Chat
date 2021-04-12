package com.example.mldcchat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mldcchat.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

     lateinit var mAuth: FirebaseAuth
    // lateinit var signoutbtn: Button
     val pager by lazy {
         findViewById<ViewPager2>(R.id.viewPager)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(
            findViewById<TabLayout>(R.id.tabs),
            pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, pos ->
                when (pos) {
                    0 -> tab.text = "Chats"
                    1 -> tab.text = "User"
                }

            }
        ).attach()


          mAuth = FirebaseAuth.getInstance()
      //   signoutbtn = findViewById(R.id.signOut)

        var currentUser = mAuth.currentUser
        if(currentUser==null){
            startActivity(Intent(applicationContext, PhoneNumber::class.java))
              finish()
        }

       /*  signoutbtn.setOnClickListener {
            mAuth.signOut()
           startActivity(Intent(this, PhoneNumber::class.java))
             finish()
       }  */
    }

}