package com.tanvir.advanceanimations

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ContentInfoCompat
import com.tanvir.advanceanimations.databinding.ActivityMainBinding
import com.tanvir.advanceanimations.utils.sendNotification

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    var downloadUrl = ""
    private lateinit var notificationManager: NotificationManager
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name)
        )
        val selectedButton1 = binding.include.radioButtonGroup.checkedRadioButtonId


        binding.include.radioButtonGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.radioButton) {
                Log.e("sleceted1", selectedButton1.toString())
                downloadUrl = URL1
            }
            if (i == R.id.radioButton2) {
                Log.e("sleceted2", selectedButton1.toString())
                downloadUrl = URL2

            }
            if (i == R.id.radioButton3) {
                Log.e("sleceted3", selectedButton1.toString())
                downloadUrl = URL3

            }
        }
        binding.include.customButton.setOnClickListener {
            notificationManager = getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.cancelAll()
            val selectedButton = binding.include.radioButtonGroup.checkedRadioButtonId
            if (selectedButton != -1) {
                download(downloadUrl)

            } else {
                Toast.makeText(
                    applicationContext, "On button click :" +
                            " nothing selected",
                    Toast.LENGTH_SHORT
                ).show()
                binding.include.customButton.nothingIsSelected()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "download is complete"


            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
               when(getDmStatus()){
                   DownloadManager.STATUS_SUCCESSFUL -> {
                       sendCustomNotification("Success $downloadUrl")
                       binding.include.customButton.hasCompletedDownload()
                   }
                   DownloadManager.STATUS_FAILED -> {
                       Log.e("asdasd","error")
                       sendCustomNotification("Failed $downloadUrl")
                       binding.include.customButton.hasFailedDownload()
                   }
                   else ->{
                       Log.e("not success", DownloadManager.ERROR_UNKNOWN.toString())
                       binding.include.customButton.hasCompletedDownload()
                   }
               }
            }
        }
    }

    private fun sendCustomNotification(status: String){
        notificationManager = getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            status,
            applicationContext.getString(R.string.button_loading),
            applicationContext
        )
    }

    private fun getDmStatus(): Int {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val dmQuery = DownloadManager.Query()
            .setFilterById(downloadID)
        val cursor = downloadManager.query(dmQuery)
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            Log.e("index",cursor.getInt(index).toString())
            return cursor.getInt(index)
        }
        return DownloadManager.ERROR_UNKNOWN
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download(url: String) {
        try {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } catch (e: Exception) {
            Log.e("ex", e.message.toString())
        }
    }
    companion object {
        private const val URL2 =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL1 =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val URL3 =
            "https://github.com/bumptech/glide/arcisve/master.zip"

        private const val CHANNEL_ID = "zip_download"
    }
}
