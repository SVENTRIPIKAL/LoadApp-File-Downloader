package com.udacity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.utils.SUCCESS_RESPONSE_OK
import com.udacity.utils.createChannel
import com.udacity.utils.isNotificationChannelRequired
import com.udacity.utils.sendNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadFileId: Long = 0
    private lateinit var downloadUrl: String
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager

    private lateinit var fileNameExtra: String
    private lateinit var fileSaveDirectory: String
    private var fileStatusExtra by Delegates.notNull<Boolean>()


    /**
     *  app permissions prompt -- saves user decision
     */
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // permission granted & download channel enabled
        if (isGranted && isDownloadChannelEnabled()) {

            // launch a non-blocking coroutine scope
            MainScope().launch {

                println("MAINSCOPE.LAUNCH")

                // start button animation
                beginButtonAnimation(true)

                // execute this blocking coroutine scope in a separate background thread
                withContext(Dispatchers.IO) {

                    println("DISPATCHERS.IO")

                    try {
                        // check if url returns a response status code
                        // THROWS - java.net.UnknownHostException
                        //          java.net.MalformedURLException
                        val statusCode = getHttpResponseStatusCode()

                        println("getHttpResponseStatusCode PASSED")

                        // return to main thread and process code
                        runOnUiThread {
                            if (statusCode == SUCCESS_RESPONSE_OK) {
                                // execute file download
                                downloadFile()

                            } else {

                                // launch a non-blocking coroutine scope
                                MainScope().launch {
                                    // end animation
                                    beginButtonAnimation(false)
                                }

                                // display HTTP response status code
                                Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.toast_http_response, statusCode.toString()),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        // catch exceptions
                    } catch (exception: Exception) {

                        // return to main thread and process exception
                        runOnUiThread {

                            // launch a non-blocking coroutine scope
                            MainScope().launch {
                                // end animation
                                beginButtonAnimation(false)
                            }

                            when (exception) {
                                // java.net.MalformedURLException
                                is MalformedURLException -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.toast_malformed_url_exception),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // java.net.UnknownHostException
                                is UnknownHostException -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.toast_unknown_host_exception),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // print exception & message to console
                                else -> {
                                    println("$exception :: ${exception.message}")
                                }
                            }
                        }
                    }
                }
            }
        }

        else // notify permissions required
            showSettingsDialog()
    }


    /**
     *  continues to run the UI animation on
     *  the Main thread. This block is required
     *  to live inside the MainScope().Launch
     *  block in order for the animation to
     *  await the completion of the requestPermissionLauncher
     *  sequence just before this. Otherwise, the animation
     *  & download sequence will execute automatically
     *  & user permissions are never requested by the OS.
     */
    private fun beginButtonAnimation(boolean: Boolean) {
        binding.includeMain.customButton.apply {
            when (boolean) {
                true -> {
                    updateButtonUI(ButtonState.UnClicked)   // set ui to default
                    isClickable = false                     // disable button clicks
                    startAnimation()                        // begin button animation
                }
                else -> {
                    updateButtonUI(ButtonState.Completed)   // end button animation
                    isClickable = true                      // enable button clicks
                }
            }
        }
    }


    /**
     *  returns the HTTP response status code of
     *  the provided URL if connection is valid.
     *  THROWS - java.net.UnknownHostException
     *           java.net.MalformedURLException
     */
    private fun getHttpResponseStatusCode(): Int {
        val url = URL(downloadUrl)
        val connection = url.openConnection() as HttpURLConnection
        return connection.responseCode
    }


    /**
     *  downloads a file from provided URL
     *  via Download Manager by queuing
     *  the request and updating the
     *  download file id
     */
    private fun downloadFile() {
        // extract file extension & title
        val space = getString(R.string.text_space)
        val period = getString(R.string.text_period)
        val ext = downloadUrl.substringAfterLast(period)
        val title = fileNameExtra.substringBefore(space).plus("$period$ext")

        // download query   [set title, description, directory, visibility]
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle(title)
            .setDescription(downloadUrl)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        // que download & update downloadId
        downloadFileId = downloadManager.enqueue(request)
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
            Toast.LENGTH_SHORT
        ).show()
    }


    /**
     *  override of the OS back button
     *  to allow exiting of the app
     *  via a dialog confirmation sequence
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showExitDialog()
    }


    /**
     *  dialog that prevents the user
     *  from accidentally exiting the
     *  app via the OS back button
     */
    private fun showExitDialog() {

        // dialog builder
        val builder = AlertDialog.Builder(this)

        // title
        builder.setTitle(getString(R.string.dialog_title_exit))
        // message
        builder.setMessage(getString(R.string.dialog_message_exit))

        // exit choice
        builder.setPositiveButton(getString(R.string.dialog_positive_exit)) { dialog, _ ->
            // dismiss the dialog
            dialog.dismiss()

            // finish the activity
            this.finish()

            // exit the app -- back used since Activity is always root stack
            onBackPressedDispatcher.onBackPressed()
        }

        // cancel choice
        builder.setNegativeButton(getString(R.string.dialog_negative_text)) { dialog, _ ->
            // close the dialog
            dialog.cancel()
        }

        // show dialog
        builder.show()
    }


    /**
     *  assign & create download & notification services
     */
    private fun registerSystemServices() {
        // assign file save location
        fileSaveDirectory = getString(
            R.string.file_save_directory,
            Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_DOWNLOADS
        )

        // retrieve os download service
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        // retrieve os notification service
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // create download notification channel -- if API 26+
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

                // revert button to default state
                refreshCustomButton()

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
                        radioCustomUrl.id -> {       // Custom Url radio button
                            showCustomUrlDialog(radioCustomUrl.text.toString())
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
                    // run check Manifest permissions
                    checkManifestPermissions()
                }
            }
        }
    }


    /**
     * enable the download button to be clicked
     * and update the button animation UI
     */
    private fun refreshCustomButton() {
        binding.includeMain.customButton.apply {
            isClickable = true
            updateButtonUI(ButtonState.UnClicked)
        }
    }


    /**
     *  dialog that allows the user
     *  to input their own URL
     *  address to download a file
     */
    private fun showCustomUrlDialog(filename: String) {
        // builder
        val builder = AlertDialog.Builder(this)

        // title
        builder.setTitle(getString(R.string.dialog_title_custom_url))

        // create an edit text that uses strings
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        // set edit text as view
        builder.setView(input)

        // update url with default edit text field
        updateDownloadInfo(filename, input.text.toString())

        // close dialog and update url with edit text field
        builder.setPositiveButton(getString(R.string.dialog_positive_submit)) { dialog, _ ->
            dialog.dismiss()
            updateDownloadInfo(filename, input.text.toString())
        }

        // display dialog
        builder.show()
    }


    /**
     *  assign download information
     *  according to radio button choice
     */
    private fun updateDownloadInfo(fileName: String, url: String) {
        fileNameExtra = fileName
        downloadUrl = url
        println(fileNameExtra)
    }


    /**
     *  check if API 26+ in order to request Notification
     *  permissions / else request Write permissions
     */
    @SuppressLint("InlinedApi")
    private fun checkManifestPermissions() {
        if (isNotificationChannelRequired()) {
            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
        } else requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
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
     *  dialog that helps navigate user to
     *  settings in order to enable the
     *  app's needed permission
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

        // on download completed
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent) {

            val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadFileId))

            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            println("$downloadFileId     $downloadId")


            // check download id and status of file
            if (downloadId == downloadFileId && cursor.moveToNext()) {

                val downloadSTATUS = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                cursor.close()

                downloadManager.apply {

                    when(downloadSTATUS) {

                        STATUS_SUCCESSFUL -> {  // update status extra & display save directory

                            updateDownloadStatus(
                                context,
                                true,
                                getString(R.string.toast_file_saved, fileSaveDirectory)
                            )
                        }

                        STATUS_FAILED -> {  // update status extra & display failure toast

                            updateDownloadStatus(
                                context,
                                false,
                                getString(R.string.toast_download_failed)
                            )
                        }
                    }
                }

                // post notification
                notificationManager.sendNotification(
                    getString(R.string.notification_description), context,
                    fileNameExtra, fileStatusExtra
                )
            }
        }
    }


    /**
     *  updates file download status and creates toast according to success
     */
    private fun updateDownloadStatus(context: Context, status: Boolean, toastText: String){
        fileStatusExtra = status
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

        beginButtonAnimation(false)
    }
}