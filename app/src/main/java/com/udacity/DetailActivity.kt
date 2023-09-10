package com.udacity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.utils.FILE_NAME_EXTRA
import com.udacity.utils.FILE_STATUS_EXTRA

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // dismiss notification
//        intent.extras?.getInt(NOTIFICATION_ID.toString())?.let { notificationManager.cancel(it) }

        // print to console filename
        intent.extras?.getString(FILE_NAME_EXTRA).let { println(it) }

        // assign file name to textView
        binding.contentDetailActivity.fileNameText.text = intent.extras?.getString(FILE_NAME_EXTRA) ?: "NULL"

        // assign file status to textView
        binding.contentDetailActivity.statusText.apply {

            when(intent.extras?.getBoolean(FILE_STATUS_EXTRA)){
                true -> {
                    text = "Success"
                    setTextColor( getColor(R.color.green) )
                }
                else -> {
                    text = "Fail"
                    setTextColor( getColor(R.color.red) )
                }
            }
        }
    }
}