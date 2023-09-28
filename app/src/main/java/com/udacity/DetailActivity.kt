package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.FILE_NAME_EXTRA
import com.udacity.utils.FILE_STATUS_EXTRA
import com.udacity.utils.NOTIFICATION_ID
import com.udacity.utils.Priority
import com.udacity.utils.TAG
import com.udacity.utils.timber

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timber(TAG, "ON-CREATE :${this::class.java.simpleName}", Priority.VERBOSE)

        // layout binding
        binding = ActivityDetailBinding.inflate(layoutInflater)

        // set view & action bar
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

            timber(TAG, "$it :${this::class.java.simpleName}", Priority.INFO)
        }

        // assign file status and color to textView
        binding.contentDetailActivity.statusText.apply {

            when(intent.extras?.getBoolean(FILE_STATUS_EXTRA)){
                true -> {
                    text = getString(R.string.file_status_success)
                    setTextColor( Color.GREEN )
                }
                else -> {
                    text = getString(R.string.file_status_fail)
                    setTextColor( Color.RED )
                }
            }
        }

        // assign FAB button click listener
        binding.contentDetailActivity.extendedFab.setOnClickListener {
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


    /**
     * Lifecycle methods
     */
    override fun onStart() {
        super.onStart()
        timber(TAG, "ON-START :${this::class.java.simpleName}", Priority.INFO)      // blue
    }

    override fun onResume() {
        super.onResume()
        timber(TAG, "ON-RESUME :${this::class.java.simpleName}", Priority.DEBUG)    // green
    }

    override fun onPause() {
        super.onPause()
        timber(TAG, "ON-PAUSE :${this::class.java.simpleName}", Priority.INFO)      // blue
    }

    override fun onStop() {
        super.onStop()
        timber(TAG, "ON-STOP :${this::class.java.simpleName}", Priority.VERBOSE)    // white
    }

    override fun onDestroy() {
        super.onDestroy()
        timber(TAG, "ON-DESTROY :${this::class.java.simpleName}", Priority.ERROR)   // red
    }
}