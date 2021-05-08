package com.camo.kripto.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this))

        //TODO tab adapter for different settings



        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_setting_container, MarketSettingsFragment()).commit()
        setContentView(binding.root)
    }
}