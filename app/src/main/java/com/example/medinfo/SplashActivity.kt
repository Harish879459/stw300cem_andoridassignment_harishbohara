package com.example.medinfo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        CoroutineScope(Dispatchers.IO).apply {
            launch {
                delay(5000)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).cancel()
    }
}