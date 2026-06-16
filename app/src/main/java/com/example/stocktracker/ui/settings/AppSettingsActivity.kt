package com.example.stocktracker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stocktracker.databinding.ActivityAppSettingsBinding

class AppSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
