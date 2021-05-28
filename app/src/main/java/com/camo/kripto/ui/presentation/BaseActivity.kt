package com.camo.kripto.ui.presentation

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import com.camo.kripto.ui.presentation.home.MainActivity
import com.camo.kripto.utils.ThemeUtil
import javax.inject.Inject


open class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val theme: Int =
            ThemeUtil.getThemeId(sharedPreferences.getString("pref_theme", ThemeUtil.THEME_RED))
        setTheme(theme)
        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when(key){
                "pref_theme" -> {
                    TaskStackBuilder.create(this)
                        .addNextIntent(Intent(this, MainActivity::class.java))
                        .addNextIntent(this.intent)
                        .startActivities()
                }
            }
        }
    }

}