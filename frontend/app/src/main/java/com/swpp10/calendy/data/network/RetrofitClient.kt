package com.swpp10.calendy.data.network

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


object RetrofitClient {
    // https 사용 시 javax.net.ssl.SSLException: Unable to parse TLS packet header
    private const val BASE_URL =
        "http://ec2-43-201-66-166.ap-northeast-2.compute.amazonaws.com:3000"

    val instance: CalendyServerApi by lazy {
        // Create Retrofit Instance
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        // Request: Any -> RequestBody
        // Response: ResponseBody -> String
        val requestGsonConverterFactory = object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
            ): Converter<ResponseBody, *> {
                return Converter { value: ResponseBody -> value.string() }
            }

            override fun requestBodyConverter(
                type: Type,
                parameterAnnotations: Array<out Annotation>,
                methodAnnotations: Array<out Annotation>,
                retrofit: Retrofit
            ): Converter<*, RequestBody>? = GsonConverterFactory.create().requestBodyConverter(
                type, parameterAnnotations, methodAnnotations, retrofit
            )
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(requestGsonConverterFactory)
            .build()

        retrofit.create(CalendyServerApi::class.java)
    }
}