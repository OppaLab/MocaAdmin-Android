package com.oppalab.moca.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConnection {
    val URL = "http://192.168.22.156:8080"
    val retrofit = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val server = retrofit.create(RetrofitService::class.java)
}