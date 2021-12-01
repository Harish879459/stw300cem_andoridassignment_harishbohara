package com.example.medinfo.response

import com.example.medinfo.entity.User

data class UserResponse(
    val success: Boolean? = null,
    val data: User? = null,
    val token: String? = null,
    val message: String? = null
)
