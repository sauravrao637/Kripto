package com.camo.kripto.ui.presentation.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //TODO tab adapter for different settings


        supportActionBar?.title = "Kripto Settings"
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_setting_container, MarketSettingsFragment()).commit()
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