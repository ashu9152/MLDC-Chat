package com.example.mldcchat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatLogActivity : AppCompatActivity() {

val adapter = GroupAdapter<GroupieViewHolder>()
    val toUser: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val toUser = intent.getParcelableExtra<User>(newUserActivity.User_Key)
        supportActionBar?.title = toUser?.name
        setContentView(R.layout.activity_chat_log)

        //setDummy()
        listenForMessage()

        send_button_chat_log.setOnClickListener {
            Log.d("Chatlog", "Attempt to Send Message")
            performSendMessage()
        }
    }

    private fun listenForMessage(){
        val ref = FirebaseDatabase.getInstance().getReference("/message")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chatMesssage = snapshot.getValue(ChatMessage::class.java)
                recyclerview_chat_log.adapter = adapter


                if (chatMesssage !=null) {
                    Log.d("Chatlog", chatMesssage.text.toString())
                    val fromId = FirebaseAuth.getInstance().uid
                    if (chatMesssage.fromId == FirebaseAuth.getInstance().uid) {
                       // val currentUser = MainActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMesssage.text/*,currentUser*/))

                    }else{
                        adapter.add(ChatToItem(chatMesssage.text, toUser!!))
                    }
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun performSendMessage(){

        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(newUserActivity.User_Key)
        val toId = user!!.id
        val ref = FirebaseDatabase.getInstance().getReference("/message").push()

        if (fromId == null) return

        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis()/1000 )
        ref.setValue(chatMessage)
                .addOnSuccessListener {
            Log.d("Chatlog", "Saved our chat message: ${ref.key}")
        }
    }

}

class ChatFromItem(val text: String, /*val user: User*/): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from_View2.text = text

    /*    val uri = user.imageUrl
        val targetImageView = viewHolder.itemView.image_from_View
        Picasso.get().load(uri).into(targetImageView)*/
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to_View.text = text

        val uri = user.imageUrl
        val targetImageView = viewHolder.itemView.image_to_View
        Picasso.get().load(uri).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}