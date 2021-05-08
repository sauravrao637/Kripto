package com.camo.kripto.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(LayoutInflater.from(this))
        supportActionBar?.title = "About Kripto"
        setContentView(binding.root)
    }
}