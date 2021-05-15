package com.camo.kripto.ui.presentation.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.camo.kripto.databinding.ActivityAboutBinding
import com.camo.kripto.utils.Extras
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.title = "About Kripto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvAboutVersion.text = Extras.getAppVersion(this)

        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}