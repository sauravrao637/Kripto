package com.camo.kripto.ui.presentation.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.app.TaskStackBuilder
import androidx.work.*
import com.camo.kripto.Constants
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySettingsBinding
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.ui.presentation.home.MainActivity
import com.camo.kripto.utils.PreferenceKeys.CURRENCY
import com.camo.kripto.utils.PreferenceKeys.ORDERING
import com.camo.kripto.utils.PreferenceKeys.PERCENTAGE_CHANGE
import com.camo.kripto.utils.PreferenceKeys.THEME
import com.camo.kripto.works.SyncLocalWorker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var workManager: WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //TODO tab adapter for different settings
        workManager = WorkManager.getInstance(applicationContext)
        supportActionBar?.title = this.getString(R.string.kripto_settings)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_setting_container, FragMarketSettings()).commit()
        setContentView(binding.root)
        setupUI()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                THEME, CURRENCY, PERCENTAGE_CHANGE, ORDERING -> {
                    TaskStackBuilder.create(this)
                        .addNextIntent(Intent(this, MainActivity::class.java))
                        .addNextIntent(this.intent)
                        .startActivities()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun setupObservers() {
        workManager.getWorkInfosForUniqueWorkLiveData(Constants.SYNC_ALL_DATA_UNIQUE_WORK_NAME)
            .observe(this, {
                if (it != null && it.isNotEmpty()) {
                    when (it[0]?.state) {
                        WorkInfo.State.ENQUEUED -> {
                            binding.btnSync.isEnabled = false
                        }
                        WorkInfo.State.CANCELLED -> {
                            Snackbar.make(
                                binding.root,
                                this@SettingsActivity.getString(R.string.cancelled_sync_curse),
                                Snackbar.LENGTH_LONG
                            ).show()
                            binding.btnSync.isEnabled = true
                        }
                        WorkInfo.State.SUCCEEDED -> binding.btnSync.isEnabled = true
                        WorkInfo.State.FAILED -> {
                            binding.btnSync.isEnabled = true
                        }
                        WorkInfo.State.RUNNING -> binding.btnSync.isEnabled = false
                        else -> {

                        }
                    }
                }
            })
    }

    private fun setupUI() {
        binding.btnSync.setOnClickListener {
            setupForFirstTime()
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun setupForFirstTime() {
        val syncWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncLocalWorker>().build()

        workManager.enqueueUniqueWork(
            Constants.SYNC_ALL_DATA_UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
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