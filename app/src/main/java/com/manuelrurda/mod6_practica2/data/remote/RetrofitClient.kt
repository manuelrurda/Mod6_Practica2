package com.manuelrurda.mod6_practica2.data.remote

import com.manuelrurda.mod6_practica2.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
    }

    val retrofit: CarsAPI by lazy {
        Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarsAPI::class.java)
    }
}