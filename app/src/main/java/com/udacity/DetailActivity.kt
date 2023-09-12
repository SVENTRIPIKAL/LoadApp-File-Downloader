package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.FILE_NAME_EXTRA
import com.udacity.utils.FILE_STATUS_EXTRA
import com.udacity.utils.NOTIFICATION_ID

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // dismiss notification
        intent.extras?.getInt(NOTIFICATION_ID.toString())?.let {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(it)
        }

        // assign file name to textView
        intent.extras?.getString(FILE_NAME_EXTRA).let {
            binding.contentDetailActivity.fileNameText.text = it
            println(it)
        }

        // assign file status and color to textView
        binding.contentDetailActivity.statusText.apply {

            when(intent.extras?.getBoolean(FILE_STATUS_EXTRA)){
                true -> {
                    text = getString(R.string.file_status_success)
                    setTextColor( getColor(R.color.green) )
                }
                else -> {
                    text = getString(R.string.file_status_fail)
                    setTextColor( getColor(R.color.red) )
                }
            }
        }

        // assign FAB button click listener
        binding.extendedFab.setOnClickListener {
            // activate return to prior screen as a back press
            onBackPressedDispatcher.onBackPressed()
        }
    }


    /**
     *  override of the OS back button
     *  to disable functionality
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // do nothing when OS back is pressed - returning is done via FAB
    }
}