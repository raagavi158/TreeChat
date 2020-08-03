package edu.stanford.cs193a.raagavi_hsieh64
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_channel_list.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChannelListActivity : AppCompatActivity() {
    lateinit var fb: DatabaseReference
    lateinit var chat: String
    lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_list)
        userName = intent.getStringExtra("account")
        FirebaseApp.initializeApp(this)
        fb = FirebaseDatabase.getInstance().getReference("treechat")
        fb.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "fix it")
            }
         /*Listener that checks for any data change in the database and prints
          * out all the channel names in the database to the screen by adding
          * them to  a arraylist and representing the array list in a listview */
            override fun onDataChange(data: DataSnapshot) {
                val loopChan: Iterable<DataSnapshot> = data.children
                var listItems = ArrayList<String>()
                for (channel: DataSnapshot in loopChan) {
                    listItems.add("#${channel.key.toString()}")
                }
                val adapter = ArrayAdapter(this@ChannelListActivity, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter
                listView.setOnItemClickListener{ _, _, index, _->
                    chat = listItems[index]!!
                    val myIntent = Intent(this@ChannelListActivity, ChannelActivity::class.java)
                    myIntent.putExtra("channelName", chat)
                    myIntent.putExtra("userName", userName)
                    startActivity(myIntent) //moving to activity associated with the channel
                }
            }
        })
    }

/* The create channel method allows the user to create a new
* channel with its description and adds it to the database
* along with default messages and details */

   fun createChannel(view: View) {
       var newChannel: String = editText.text.toString()
       var desc = editText2.text.toString()
       val chanName = fb.child(newChannel)
       chanName.child("name").setValue(newChannel)
       chanName.child("description").setValue(desc)
       chanName.child("users").setValue("")
       editText.text.clear()
       editText2.text.clear()
       chanName.child("messages").setValue(0)
       val msgs = chanName.child("messages")
       val newMsg = msgs.child("0")
       sendMsg("treebot", "Welcome to the #$newChannel channel!", newMsg)
   }

    @TargetApi(Build.VERSION_CODES.O)

/* The sendMsg method creates a message field in the
 * respective channel node of the database and enters it
  * with appropriate details */
   fun sendMsg(from: String, text: String, db :DatabaseReference)
{
    db.child("from").setValue(from)
    db.child("text").setValue(text)
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val formatted = current.format(formatter)
    db.child("timestamp").setValue(formatted)
}
}
