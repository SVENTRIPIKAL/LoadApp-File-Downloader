package com.udacity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.DownloadManager.STATUS_FAILED
import android.app.DownloadManager.STATUS_SUCCESSFUL
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.createChannel
import com.udacity.utils.isNotificationChannelRequired
import com.udacity.utils.sendNotification
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var urlDownloadId: Long = 0
    private lateinit var downloadUrl: String
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager
//    private lateinit var pendingIntent: PendingIntent
//    private lateinit var action: NotificationCompat.Action

    private lateinit var fileNameExtra: String
    private var statusExtra by Delegates.notNull<Boolean>()


    /**
     *  app permissions prompt -- saves user decision
     */
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // permission granted & download channel enabled
        if (isGranted && isDownloadChannelEnabled()) {

            // inform downloading executed
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()

            // extract file extension & title
            val ext = downloadUrl.substringAfterLast(".")
            val title = fileNameExtra.substringBefore(" ").plus(".$ext")

            // download query   [set title, directory, visibility]
            val request = DownloadManager.Request( Uri.parse(downloadUrl) )
                .setTitle(title)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

            // que download & update downloadId
            urlDownloadId = downloadManager.enqueue(request)
        }

        else // notify permissions required
            showSettingsDialog()
    }


    /**
     *  go to settings prompt -- navigates user to settings
     */
    private var goToSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){}


    /**
     *  oncreate function
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //register download & notification services
        registerSystemServices()

        // register broadcast receiver
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // register ui listeners
        setUIListeners()

        // inform user to make a selection
        Toast.makeText(
            this,
            getString(R.string.toast_select_file),
            Toast.LENGTH_LONG
        ).show()
    }


    /**
     *  assign & create download & notification services
     */
    private fun registerSystemServices() {
        // retrieve os download service
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        // retrieve os notification service
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // create download notification channel -- if needed
        createChannel(
            this,
            getString(R.string.channel_id_download),
            getString(R.string.channel_name_download)
        )
    }


    /**
     *  set listeners for UI
     */
    private fun setUIListeners() {
        // main layout bindings
        binding.includeMain.apply {

            // radio group listener
            radioGroup.setOnCheckedChangeListener { _, _ ->

                // allow downloads upon selection / reset button UI
                customButton.isClickable = true
//                customButton.resetButtonUI() // reset button animation

                radioGroup.apply {
                    when(checkedRadioButtonId) {
                        radioGlide.id -> {          // Glide radio button
                            updateDownloadInfo(
                                radioGlide.text.toString(),
                                getString(R.string.url_glide),
                            )
                        }
                        radioLoadApp.id -> {        // LoadApp radio button
                            updateDownloadInfo(
                                radioLoadApp.text.toString(),
                                getString(R.string.url_loadApp)
                            )
                        }
                        radioRetrofit.id -> {       // Retrofit radio button
                            updateDownloadInfo(
                                radioRetrofit.text.toString(),
                                getString(R.string.url_retrofit)
                            )
                        }
                    }
                }

                // inform user to click download button
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.toast_click_button),
                    Toast.LENGTH_SHORT
                ).show()

                // set listener for button
                customButton.setOnClickListener {

//                    customButton.isClickable = false // disable clicks until animation finishes
                    download() // run download function
                }
            }
        }
    }


    /**
     *  assign download information
     *  according to radio button choice
     */
    private fun updateDownloadInfo(fileName: String, url: String) {
        fileNameExtra = fileName
        downloadUrl = url
        statusExtra = true
        println(fileNameExtra)
    }


    /**
     *  execute download service
     */
    @SuppressLint("InlinedApi")
    private fun download() {
        // launch permission prompt to enable notifications
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }


    /**
     *  check if API 26+ channel is enabled || API < 26 notifications enabled
     */
    private fun isDownloadChannelEnabled(): Boolean =
        // check if API 26+ -- channels required in API 26+
        if (isNotificationChannelRequired()) {

            // check if download channel is enabled
            notificationManager.getNotificationChannel(
                getString(R.string.channel_id_download)
            ).importance != NotificationManager.IMPORTANCE_NONE

            // if API < 26 -- check if notifications are enabled
        } else NotificationManagerCompat.from(this).areNotificationsEnabled()


    /**
     * settings dialog to navigate user to settings
     */
    private fun showSettingsDialog() {
        // dialog builder
        val builder = AlertDialog.Builder(this)

        // title via API
        val a = if (isNotificationChannelRequired())
                        getString(R.string.channel_name_download)
                else    getString(R.string.dialog_title_low_api)

        // title
        builder.setTitle(getString(R.string.dialog_title, a))

        // message via API
        val b = if (isNotificationChannelRequired())
                        getString(R.string.dialog_message_high_api)
                else    getString(R.string.dialog_message_low_api)

        // message
        builder.setMessage(
            getString(R.string.dialog_message, getString(R.string.app_name), b)
        )

        // accept & navigate user to settings
        builder.setPositiveButton(getString(R.string.dialog_positive_text)) { dialog, _ ->

            dialog.dismiss()        // dismiss dialog

            // create intent
            val intent = if (isNotificationChannelRequired()) {  // API 26+

                // create app notification settings intent
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

            } else {    // API < 26

                // create uri
                val uri = Uri.fromParts(
                    getString(R.string.uri_scheme_low_api),
                    packageName, null
                )

                // create app settings intent
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            }

            // launch activity result with intent
            goToSettingsLauncher.launch(intent)
        }

        // decline & cancel dialog
        builder.setNegativeButton(getString(R.string.dialog_negative_text)){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()  // display dialog
    }


    /**
     *  download broadcast receiver
     */
    private val receiver = object : BroadcastReceiver() {

        // on download complete
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent) {

            val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(urlDownloadId))

            val downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            println("$urlDownloadId     $downloadID")


            // check download id and status of file
            if (downloadID == urlDownloadId && cursor.moveToNext()) {

                val downloadSTATUS = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                cursor.close()

                downloadManager.apply {

                    when(downloadSTATUS) {

                        STATUS_SUCCESSFUL -> {  // send notification
                            notificationManager.sendNotification(
                                getString(R.string.notification_description), context
                            )
                        }

                        STATUS_FAILED -> {  // display toast
                            Toast.makeText(context, "Download Failed...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}