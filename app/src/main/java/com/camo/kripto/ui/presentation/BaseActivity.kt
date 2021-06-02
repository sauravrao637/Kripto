package com.camo.kripto.ui.presentation

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        supportActionBar?.elevation = 0f

    }
}