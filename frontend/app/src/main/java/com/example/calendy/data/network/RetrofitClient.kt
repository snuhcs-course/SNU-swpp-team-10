package com.example.calendy.data.network

import com.example.calendy.data.CalendyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // https 사용 시 javax.net.ssl.SSLException: Unable to parse TLS packet header
    private const val BASE_URL = "http://ec2-43-201-66-166.ap-northeast-2.compute.amazonaws.com:3000"

    val instance: CalendyApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CalendyApi::class.java)
    }
}