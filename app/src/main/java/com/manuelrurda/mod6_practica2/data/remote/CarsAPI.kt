package com.manuelrurda.mod6_practica2.data.remote

import com.manuelrurda.mod6_practica2.data.remote.model.CarDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CarsAPI {
    @GET("/cars")
    suspend fun getCars(): Response<List<CarDto>>

    @GET("/cars/{id}")
    suspend fun getCarById(@Path("id") id: Int): Response<CarDto>

}