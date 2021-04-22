package com.example.mldcchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_user.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class newUserActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Select User"
        setContentView(R.layout.activity_new_user)

        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        newUser.layoutManager = linearLayoutManager


        fetchUser()
    }

    companion object{
        val User_Key = "User_Key"
    }

    private fun fetchUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach {
                    Log.d("New Message", it.toString())
                    val user = it.getValue(User::class.java)
                        adapter.add(UserItem(user!!))
                }
                adapter.setOnItemClickListener { item, view ->

                    val user1 =item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(User_Key,user1.user)
                    startActivity(intent)
                    finish()
                }

                newUser.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

    class UserItem(val user: User): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.userName_textView_New_Message.text = user.name

            Picasso.get().load(user.imageUrl).into(viewHolder.itemView.imageView2)

        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }

    }
