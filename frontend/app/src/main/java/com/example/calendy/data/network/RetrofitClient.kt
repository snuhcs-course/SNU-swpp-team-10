package com.example.calendy.data.network

import com.example.calendy.data.CalendyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "YOUR_SERVER_URL"

    val instance: CalendyApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CalendyApi::class.java)
    }
}