package com.camo.kripto.ui.presentation.about

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityAboutBinding
import com.camo.kripto.remote.api.GHApiHelper
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.utils.Extras
import com.camo.kripto.utils.ThemeUtil
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    @Inject
    lateinit var ghApiHelper: GHApiHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.title = "About Kripto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvAboutVersion.text = Extras.getAppVersion(this)

        setupUI()
        setContentView(binding.root)
    }

    private fun setupUI() {
        val adapter = AboutTabAdapter(
            this, ghApiHelper
        )
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayoutActivityAbout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "About"
                1 -> tab.text = "Contributors"
            }
        }.attach()
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