package com.manuelrurda.mod6_practica2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelrurda.mod6_practica2.data.remote.model.CarDto
import com.manuelrurda.mod6_practica2.data.remote.model.getEmptyCarModel
import com.manuelrurda.mod6_practica2.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CarsViewModel: ViewModel() {
    private val _carsUiState = MutableStateFlow<UiState<List<CarDto>>>(UiState.Loading)
    val carsUiState = _carsUiState.asStateFlow()

    private val _carUiState = MutableStateFlow<UiState<CarDto>>(UiState.Loading)
    val carUiState = _carUiState.asStateFlow()

    init {
        getCars()
    }

    fun getCars() {
        _carsUiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.retrofit.getCars()
                withContext(Dispatchers.Main){
                    if (response.isSuccessful){
                        _carsUiState.value = UiState.Success(response.body() ?: emptyList())
                    }else{
                        _carsUiState.value = UiState.Error.ServerError
                    }
                }
            }catch (e: IOException){
                _carsUiState.value = UiState.Error.NetworkError
            }catch (e: Exception){
                _carsUiState.value = UiState.Error.UnexpectedError
            }
        }
    }

    fun getCarById(id:Int){
        _carUiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.retrofit.getCarById(id)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        _carUiState.value = UiState.Success(response.body() ?: getEmptyCarModel())
                    }else{
                        _carUiState.value = UiState.Error.ServerError
                    }
                }
            }catch (e: IOException){
                Log.d("CAR ERROR", "getCarById: " + e.message)
                _carUiState.value = UiState.Error.NetworkError
            }catch (e: Exception){
                _carUiState.value = UiState.Error.UnexpectedError
            }
        }
    }
}