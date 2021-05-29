package com.camo.kripto.ui.presentation.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.Observer
import androidx.work.*
import com.camo.kripto.Constants
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySettingsBinding
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.presentation.home.MainActivity
import com.camo.kripto.utils.ThemeUtil
import com.camo.kripto.works.SyncLocalWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //TODO tab adapter for different settings

        supportActionBar?.title = "Kripto Settings"
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_setting_container, FragMarketSettings()).commit()
        setContentView(binding.root)

        binding.btnSync.setOnClickListener {
            setupForFirstTime()
        }
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "pref_theme", "pref_currency", "pref_per_change_dur", "pref_order" -> {
                    TaskStackBuilder.create(this)
                        .addNextIntent(Intent(this, MainActivity::class.java))
                        .addNextIntent(this.intent)
                        .startActivities()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun setupForFirstTime() {
        val syncWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncLocalWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .build()

        val workManager = WorkManager
            .getInstance(this@SettingsActivity)

        workManager.enqueueUniqueWork(
            Constants.SYNC_ALL_DATA_UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )

        workManager.getWorkInfoByIdLiveData(syncWorkRequest.id)
            .observeForever(object : Observer<WorkInfo> {
                override fun onChanged(workInfo: WorkInfo?) {
                    var text = ""
                    if (workInfo == null) text = "Sync Failed"
                    else {
                        when (workInfo.state) {
                            WorkInfo.State.ENQUEUED -> text = "SyncScheduled"
                            WorkInfo.State.SUCCEEDED -> text = "Sync Successful"
                            WorkInfo.State.RUNNING -> text = "Sync in progress"
                            WorkInfo.State.FAILED -> text = "Sync Failed"
                            WorkInfo.State.CANCELLED -> text = "Sync Cancelled"
                            else -> {
                            }
                        }
                    }
                    Toast.makeText(this@SettingsActivity, text, Toast.LENGTH_SHORT).show()
                    workManager.getWorkInfoByIdLiveData(syncWorkRequest.id)
                        .removeObserver(this)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}