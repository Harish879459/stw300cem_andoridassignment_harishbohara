package com.example.medinfo.entity

import java.util.*
import kotlin.collections.ArrayList

data class Medicine(
    val _id: String? = null,
    val name: String? = null,
    val uses: ArrayList<String>? = null,
    val description: String? = null
)
