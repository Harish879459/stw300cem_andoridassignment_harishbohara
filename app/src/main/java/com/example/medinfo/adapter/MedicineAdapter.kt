package com.example.medinfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medinfo.R
import com.example.medinfo.entity.Medicine

class MedicineAdapter(var medicineList: MutableList<Medicine>, val context: Context) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {
    inner class MedicineViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<TextView>(R.id.name)
        val uses = v.findViewById<TextView>(R.id.uses)
        val description = v.findViewById<TextView>(R.id.description)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        return MedicineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicineList[position]

        holder.name.text = medicine.name.toString()
        holder.uses.text = medicine.uses.toString()
        holder.description.text = medicine.description.toString()

    }

    override fun getItemCount(): Int {
        return medicineList.size
    }
}