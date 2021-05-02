package com.camo.kripto.ui.preferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.camo.kripto.R
import com.camo.kripto.ui.MarketSettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, MarketSettingsFragment())
            .commit()
    }
}