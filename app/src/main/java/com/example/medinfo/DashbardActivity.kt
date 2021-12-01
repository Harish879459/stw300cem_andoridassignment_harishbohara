package com.example.medinfo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.medinfo.databinding.ActivityDashbardBinding

class DashbardActivity : AppCompatActivity() {
    private lateinit var view: ActivityDashbardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityDashbardBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        view.contact.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        view.medicine.setOnClickListener {
            startActivity(Intent(this, MedicineActivity::class.java))
        }

        view.logout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}