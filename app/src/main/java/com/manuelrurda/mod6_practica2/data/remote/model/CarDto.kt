package com.manuelrurda.mod6_practica2.data.remote.model

data class CarDto(
    val id:Int,
    val make:String,
    val model:String,
    val year:Int,
    val color:String,
    val price:Double,
    val imageUrl:String,
    val videoUrl:String
)

fun getEmptyCarModel(): CarDto {
    return CarDto(
        id = 0,
        make = "",
        model = "",
        year = 0,
        color = "",
        price = 0.0,
        imageUrl = "",
        videoUrl = ""
    )
}