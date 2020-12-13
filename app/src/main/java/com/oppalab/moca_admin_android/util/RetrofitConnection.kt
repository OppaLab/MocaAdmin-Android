package com.oppalab.moca.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConnection {
    val URL = "http://172.30.1.37:8080"
    val retrofit = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val server = retrofit.create(RetrofitService::class.java)
}