package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.createChannel
import com.udacity.utils.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        createChannel(this,
            getString(R.string.channel_id),
            getString(R.string.channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

//         TODO: Implement code below
        binding.includeMain.customButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

//    private fun download() {
//        val request =
//            DownloadManager.Request(Uri.parse(URL))
//                .setTitle(getString(R.string.app_name))
//                .setDescription(getString(R.string.app_description))
//                .setRequiresCharging(false)
//                .setAllowedOverMetered(true)
//                .setAllowedOverRoaming(true)
//
//        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//        downloadID =
//            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
//    }

    private fun download() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.sendNotification(
            getString(R.string.notification_description),
            this
        )
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    }
}