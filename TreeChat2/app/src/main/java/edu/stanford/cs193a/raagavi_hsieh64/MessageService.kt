package edu.stanford.cs193a.raagavi_hsieh64

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.util.Log

class MessageService : Service() {



    override fun onStartCommand(intent: Intent?, flags: Int, id: Int): Int {
        // unpack any parameters that were passed to us
        if (intent != null) {
            val msg = intent.getStringExtra("message")

            Log.d("MessageService", msg)

            val thread = Thread {

                makeNotification(msg)


                val done = Intent()
                done.action = "messagecomplete"
                done.putExtra("message", msg)
                sendBroadcast(done)
            }

            thread.start()

        }

        return START_STICKY   // stay running
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun makeNotification(msg: String) {

        val manager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager
        var builder = Notification.Builder(this)

        // new Android versions require us to create a notification "channel"
        // for the notification before we send it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)

            builder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)

        }


        builder.setContentTitle("New Message Recieved")
        builder.setContentText(msg)
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.downloadicon)



        //clicking on notification
        val intent = Intent(this, ChannelActivity::class.java)
        intent.action = "messagecomplete"
        intent.putExtra("message", msg)
        val pending = PendingIntent.getActivity(
            this, 0, intent, 0)
        builder.setContentIntent(pending)




        // send the notification
        val notification = builder.build()
        manager.notify(NOTIFICATION_ID, notification)

    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    companion object {
        // ID code that is used to launch the download notifications
        private const val NOTIFICATION_CHANNEL_ID = "CS193ADownloadService"
        private const val NOTIFICATION_ID = 1234
    }
}
