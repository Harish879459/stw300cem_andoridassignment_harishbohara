package com.example.medinfo.response

import com.example.medinfo.entity.Medicine

data class MedicineResponse(
    val success: Boolean? = null,
    var data: MutableList<Medicine>? = null
)