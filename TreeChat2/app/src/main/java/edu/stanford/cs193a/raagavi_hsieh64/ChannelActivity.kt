package edu.stanford.cs193a.raagavi_hsieh64

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_channel.*
import kotlinx.android.synthetic.main.activity_channel_list.*

class ChannelActivity : AppCompatActivity() {
    lateinit var userName: String
    lateinit var channel: String
    lateinit var userKey: String
    var nummsgs = 0
    var oldnummsgs = 0
    lateinit var fb: DatabaseReference
    lateinit var fb2: DatabaseReference
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        userName = intent.getStringExtra("userName")
        channel = intent.getStringExtra("channelName")
        var listItems = ArrayList<String>()
        channel = channel.substring(1)
        textView3.text = "#$channel"
        FirebaseApp.initializeApp(this)
        fb = FirebaseDatabase.getInstance().getReference("treechat/$channel/messages") // to get new messages
        fb2 = FirebaseDatabase.getInstance().getReference("treechat/$channel/users") // to get active users
        this.userKey = fb2.push().key!!
        if (userKey != null) {
            fb2.child(userKey).setValue(userName)
        }


        val filter = IntentFilter()
        filter.addAction("messagecomplete")
        registerReceiver(MessageReciever(), filter)


        //if theres incoming message
        fb.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "fix it")
            }
            /*Listener that checks for any data change in the database and prints
             * out all new texts along with the sender into a textView
             using iterable dataSnapshots and extracting data from each one*/
            override fun onDataChange(data: DataSnapshot) {
                val loopChan: Iterable<DataSnapshot> = data.children
                nummsgs = data.children.count()
                textView4.text = ""
                var i = 1
                for (curchannel: DataSnapshot in loopChan) {

                    textView4.text =
                        "${textView4.text}${curchannel.child("from").value}: ${curchannel.child("text").value} \n"
                    if (i > oldnummsgs && i <= nummsgs) {
                        val tmp = "New message from ${curchannel.child("from").value} in #$channel"

                        val intent = Intent(this@ChannelActivity, MessageService::class.java)
                        intent.putExtra("message", tmp)
                        startService(intent)
                    }
                    i++
                }
                oldnummsgs = nummsgs

            }
        })
        /*Listener that checks for any new users in the channel database and prints
          * out all the channel names by iterating over iterable data snapshots
          * in the database to the screen by adding them to a arraylist and representing
          * the array list in a listview */
        fb2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "fix it")
            }

            override fun onDataChange(data: DataSnapshot) {
                val loopUsers: Iterable<DataSnapshot> = data.children
                val numusers = data.children.count()
                for (user: DataSnapshot in loopUsers) {
                    var curkey: String = user.key.toString()
                    listItems.add("${user.value}")

                }
                val adapter = ArrayAdapter(this@ChannelActivity, android.R.layout.simple_list_item_1, listItems)
                listView2.adapter = adapter
            }
        })
    }

    /* The sendMessage method creates a message field in the
     * respective channel node of the database and enters it
      * with appropriate details by calling the sendMsg function
      * in the ChannelListActivity */

    fun sendMessage(view: View) {
        val chanobj = ChannelListActivity()
        val newMsg = fb.child("$nummsgs")
        chanobj.sendMsg(userName, editText4.text.toString(), newMsg)
        editText4.text.clear()
    }

    // On stop, this method removes the user's name from the list of
    // active users in the database
    override fun onStop() {
        super.onStop()
        var signOut = fb2.child(userKey)
        signOut.removeValue()
    }


    /*
     * This class is notified when the service makes a broadcast.
     */
    private inner class MessageReciever : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val msg = intent.getStringExtra("message")
            Log.d("DownloadReceiver", "this URL is done: $msg")
        }
    }
}

