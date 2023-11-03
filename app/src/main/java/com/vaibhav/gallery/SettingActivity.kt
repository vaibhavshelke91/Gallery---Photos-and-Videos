package com.vaibhav.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vaibhav.gallery.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding:ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        val version=BuildConfig.VERSION_NAME
        binding.versionCode.text="Gallery\nVersion $version"
    }
}