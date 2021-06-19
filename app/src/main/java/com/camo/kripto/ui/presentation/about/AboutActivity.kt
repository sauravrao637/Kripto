package com.camo.kripto.ui.presentation.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.camo.kripto.databinding.ActivityAboutBinding
import com.camo.kripto.ktx.enforceSingleScrollDirection
import com.camo.kripto.ktx.recyclerView
import com.camo.kripto.remote.api.GHApiHelper
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.utils.Extras
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.camo.kripto.R
@AndroidEntryPoint
class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    @Inject
    lateinit var ghApiHelper: GHApiHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.title = this.getString(R.string.about_kripto)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvAboutVersion.text = Extras.getAppVersion(this)

        setupUI()
        setContentView(binding.root)
    }

    private fun setupUI() {
        val adapter = AboutTabAdapter(
            this
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.recyclerView.enforceSingleScrollDirection()
        TabLayoutMediator(binding.tabLayoutActivityAbout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = this.getString(R.string.about)
                1 -> tab.text = this.getString(R.string.contributors)
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