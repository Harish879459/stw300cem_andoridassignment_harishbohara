package com.example.medinfo.repository

import com.example.medinfo.api.ApiRequest
import com.example.medinfo.api.MedicineAPI
import com.example.medinfo.api.ServiceBuilder
import com.example.medinfo.entity.Medicine
import com.example.medinfo.response.MedicineResponse

class MedicineRepository : ApiRequest() {

    private val medicineAPI = ServiceBuilder.buildService(MedicineAPI::class.java)


    suspend fun findMedicine(): MedicineResponse {
        return apiRequest {
            medicineAPI.findMedicine(ServiceBuilder.token!!)
        }
    }


}