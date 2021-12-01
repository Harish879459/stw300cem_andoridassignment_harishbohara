package com.example.medinfo.repository

import com.example.medinfo.api.ApiRequest
import com.example.medinfo.api.ServiceBuilder
import com.example.medinfo.api.UserAPI
import com.example.medinfo.entity.User
import com.example.medinfo.response.ImageResponse
import com.example.medinfo.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.Response

class UserRepository : ApiRequest() {

    private val userAPI = ServiceBuilder.buildService(UserAPI::class.java)

    //register
    suspend fun register(user: User): UserResponse {
        return apiRequest {
            userAPI.register(user)
        }
    }

    //login
    suspend fun login(email: String, password: String): UserResponse {
        return apiRequest {
            userAPI.login(email, password)
        }
    }

    //update
    suspend fun update(user: User, id: String): UserResponse {
        return apiRequest {
            userAPI.update(ServiceBuilder.token!!, user, id);
        }
    }

    //delete
    suspend fun eraseMe(): UserResponse {
        return apiRequest {
            userAPI.eraseMe(ServiceBuilder.token!!);
        }
    }

    suspend fun profile(id: String): UserResponse {
        return apiRequest {
            userAPI.profile(ServiceBuilder.token!!, id);
        }
    }

    suspend fun uploadImage(id: String, body: MultipartBody.Part): ImageResponse {
        return apiRequest {
            userAPI.uploadImage(id, body);
        }
    }

    suspend fun updateImage(id: String, body: MultipartBody.Part): ImageResponse {
        return apiRequest {
            userAPI.updateImage(ServiceBuilder.token!!, id, body);
        }
    }


}