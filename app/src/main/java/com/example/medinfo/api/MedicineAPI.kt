package com.example.medinfo.api


import com.example.medinfo.entity.Medicine
import com.example.medinfo.response.MedicineResponse
import retrofit2.Response
import retrofit2.http.*

interface MedicineAPI {

    @GET("findMedicine")
    suspend fun findMedicine(
        @Header("Authorization") token: String
    ): Response<MedicineResponse>

    @PUT("updateMedicine/{id}")
    suspend fun fav(
        @Header("Authorization") token: String,
        @Body medicine: Medicine,
        @Path("id") id: String
    ): Response<MedicineResponse>

}