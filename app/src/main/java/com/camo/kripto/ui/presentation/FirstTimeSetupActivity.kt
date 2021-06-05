package com.camo.kripto.ui.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.camo.kripto.databinding.ActivityFirstTimeSetupBinding
import com.camo.kripto.error.ErrorPanelHelper
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.presentation.home.MainActivity
import com.camo.kripto.utils.Status
import com.camo.kripto.utils.ThemeUtil.THEME_RED
import com.camo.kripto.works.SyncLocalWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class FirstTimeSetupActivity : BaseActivity() {

    private lateinit var binding: ActivityFirstTimeSetupBinding
    private lateinit var errorPanel: ErrorPanelHelper
    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstTimeSetupBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.hide()
        setContentView(binding.root)
        errorPanel = ErrorPanelHelper(binding.root, ::shouldSync)
        incLaunchCount()
        shouldSync()
    }

    override fun onPause() {
        super.onPause()
        decLaunchCount()
    }

    private fun shouldSync() {
        lifecycleScope.launchWhenStarted {
            var count: Int
            delay(500)
            withContext(Dispatchers.IO) {
                count = repository.getCurrCount()
            }
            if (count == 0) {
                val editor = sharedPreferences.edit()
                editor.apply{
                    putString("pref_currency","inr")
                    putString("pref_order","market_cap_desc")
                    putString("pref_def_frag","0")
                    putString("pref_per_change_dur","1h")
                    putString("pref_theme",THEME_RED)
                }
                editor.apply()
                repository.pingCG().collect {
                    when (it.status) {
                        Status.LOADING -> {
                            binding.pbFtsa.visibility = View.VISIBLE
                            binding.tvFirstTimeSetup.visibility = View.VISIBLE
                            binding.errorPanel.root.visibility = View.INVISIBLE
                            errorPanel.hide()
                            errorPanel.dispose()
                        }
                        Status.ERROR -> {
                            binding.tvFirstTimeSetup.visibility = View.INVISIBLE
                            binding.errorPanel.root.visibility = View.VISIBLE
                            errorPanel.showError(it.errorInfo)
                            binding.pbFtsa.visibility = View.INVISIBLE
                        }
                        Status.SUCCESS -> {
                            binding.errorPanel.root.visibility = View.INVISIBLE
                            binding.tvFirstTimeSetup.visibility = View.INVISIBLE
                            errorPanel.hide()
                            errorPanel.dispose()
                            setupForFirstTime()
                            goToMainActivity()
                        }
                    }
                }
            } else {
                goToMainActivity()
            }
        }
    }

    private fun goToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        this.startActivity(i)
        this.finish()
    }

    private var firstTimeJob: Job? = null
    private fun setupForFirstTime() {
        firstTimeJob?.cancel()
        firstTimeJob = GlobalScope.launch(Dispatchers.IO) {
            val syncWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<SyncLocalWorker>()
                    .build()
            WorkManager
                .getInstance(this@FirstTimeSetupActivity)
                .enqueue(syncWorkRequest)
        }
    }

    private fun incLaunchCount() {
        var c: Int = sharedPreferences.getInt("numRun", 0)
        c++
        sharedPreferences.edit().putInt("numRun", c).apply()
    }

    private fun decLaunchCount() {
        if (!isFinishing) {
            var c = sharedPreferences.getInt("numRun", 0)
            c--
            sharedPreferences.edit().putInt("numRun", c).apply()
        }
    }
}