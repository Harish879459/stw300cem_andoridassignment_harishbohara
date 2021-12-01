package com.example.medinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medinfo.adapter.MedicineAdapter
import com.example.medinfo.databinding.ActivityDashbardBinding
import com.example.medinfo.databinding.ActivityMedicineBinding
import com.example.medinfo.repository.MedicineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MedicineActivity : AppCompatActivity() {

    private lateinit var view: ActivityMedicineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMedicineBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.rv.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).apply {
            launch {
                val medRepo = MedicineRepository()
                val response = medRepo.findMedicine()
                if (response.success == true) {
                    response.data!!.reverse()
                    withContext(Main) {
                        val adpater = MedicineAdapter(response.data!!, this@MedicineActivity)
                        view.rv.adapter = adpater
                    }
                }
            }
        }
    }
}