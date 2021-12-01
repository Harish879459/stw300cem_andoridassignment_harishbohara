package com.example.medinfo.api

import com.example.medinfo.entity.User
import com.example.medinfo.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {

    @POST("register")
    suspend fun register(
        @Body user: User
    ): Response<UserResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") username: String,
        @Field("password") password: String
    ): Response<UserResponse>

    @PUT("update/{id}")
    suspend fun update(
        @Header("Authorization") token: String,
        @Body user: User,
        @Path("id") id: String
    ): Response<UserResponse>

    @DELETE("erase/{id}")
    suspend fun eraseMe(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @Multipart
    @POST("uploadProfileImage/{id}")
    suspend fun uploadImage(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<ImageResponse>


    @GET("profile/{id}")
    suspend fun profile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<UserResponse>


    @Multipart
    @POST("uploads/{id}")
    suspend fun updateImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<ImageResponse>

}