package com.example.calendy.data.network

import com.example.calendy.data.CalendyApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    // https 사용 시 javax.net.ssl.SSLException: Unable to parse TLS packet header
    private const val BASE_URL = "http://ec2-43-201-66-166.ap-northeast-2.compute.amazonaws.com:3000"

    val instance: CalendyApi by lazy {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CalendyApi::class.java)
    }
}