package com.udacity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.createChannel
import com.udacity.utils.isNotificationChannelRequired
import com.udacity.utils.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    // app permissions prompt -- saves user decision
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // permission granted & download channel enabled
        if (isGranted && isDownloadChannelEnabled()) {

            // send notification
            notificationManager.sendNotification(
                getString(R.string.notification_description),
                this
            )
        }
            // notify user permission required
        else showSettingsDialog()
    }


    // go to settings prompt -- navigates user to settings
    private var goToSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){}



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //register notification services
        registerNotificationServices()

        // register broadcast receiver
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

//         TODO: Implement code below
        // download button onclick listener
        binding.includeMain.customButton.setOnClickListener {
            download()
        }
    }



    // assign & create notification services
    private fun registerNotificationServices() {

        // retrieve os notification service
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // create download notification channel -- if needed
        createChannel(
            this,
            getString(R.string.channel_id_download),
            getString(R.string.channel_name_download)
        )
    }


    // execute download service
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun download() {

        // launch permission prompt to enable notifications
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }


    // check if API 26+ channel is enabled || API < 26 notifications enabled
    private fun isDownloadChannelEnabled(): Boolean =

        // check if API 26+ -- channels required in API 26+
        if (isNotificationChannelRequired()) {

            // check if download channel is enabled
            notificationManager.getNotificationChannel(
                getString(R.string.channel_id_download)
            ).importance != NotificationManager.IMPORTANCE_NONE

            // if API < 26 -- check if notifications are enabled
        } else NotificationManagerCompat.from(this).areNotificationsEnabled()


    // settings dialog to navigate user to settings
    private fun showSettingsDialog() {

        // dialog builder
        val builder = AlertDialog.Builder(this)

        // title via API
        val a = if (isNotificationChannelRequired()) getString(R.string.channel_name_download) else "Notifications"

        // title
        builder.setTitle("$a Needed")

        // message via API
        val b = if (isNotificationChannelRequired()) "channel" else "feature"

        // message
        builder.setMessage("${getString(R.string.app_name)} requests this $b be enabled in your app settings.")

        // accept & navigate user to settings
        builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->

            dialog.dismiss()        // dismiss dialog

            // create intent
            val intent = if (isNotificationChannelRequired()) {  // API 26+

                // create app notification settings intent
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

            } else {    // API < 26

                // create uri
                val uri = Uri.fromParts("package", packageName, null)

                // create app settings intent
                Intent(Settings.ACTION_APPLICATION_SETTINGS, uri)
            }

            // launch activity result with intent
            goToSettingsLauncher.launch(intent)
        }

        // decline & cancel dialog
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()  // display dialog
    }



    // broadcast receiver
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


    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    }
}