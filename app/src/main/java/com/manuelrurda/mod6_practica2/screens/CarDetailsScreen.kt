package com.manuelrurda.mod6_practica2.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.manuelrurda.mod6_practica2.R
import com.manuelrurda.mod6_practica2.data.remote.model.CarDto
import com.manuelrurda.mod6_practica2.viewmodels.CarsViewModel
import com.manuelrurda.mod6_practica2.viewmodels.UiState
import com.manuelrurda.mod6_practica2.views.ErrorCard
import com.manuelrurda.mod6_practica2.views.LoadingAnimation
import com.manuelrurda.mod6_practica2.views.VideoPlayer

@Composable
fun CarDetailsScreen(id: Int, viewModel: CarsViewModel) {
    val carUiState by viewModel.carUiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.getCarById(id)
    }

    Box(
        modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center) {
        when(carUiState){
            is UiState.Loading -> {
                LoadingAnimation(
                    circleColor = Color.Black,
                    circleSize = 20.dp,
                    width = 110.dp
                )
            }
            is UiState.Success -> {
                CarDetailsCard(car = (carUiState as UiState.Success).data)
            }
            is UiState.Error -> {
                val message = when(carUiState as UiState.Error){
                    is UiState.Error.ServerError -> stringResource(id = R.string.error_server_error)
                    is UiState.Error.NetworkError -> stringResource(id = R.string.error_network_error)
                    is UiState.Error.UnexpectedError -> stringResource(id = R.string.error_unexpected_error)
                }
                ErrorCard(
                    message = message,
                    onTap = {
                        viewModel.getCarById(id)
                    }
                )
            }
        }
    }
}

@Composable
private fun CarDetailsCard(car: CarDto){
    val hasImage = car.imageUrl.isNotEmpty()
    val avatar = rememberImagePainter(data = car.imageUrl)

    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = car.make,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp))
            Text(text = "${car.year} • ${car.model}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = if (hasImage) avatar
                else painterResource(R.drawable.car_placeholder),
                contentDescription = stringResource(id = R.string.description_car),
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(color = Color.White)

            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = stringResource(id = R.string.price, car.price),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp))
            Text(car.color, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(id = R.string.video, car.price),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp))
            Spacer(modifier = Modifier.height(5.dp))
            VideoPlayer(
                context = LocalContext.current,
                videoUrl = car.videoUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun CarDetailsScreenPreview() {
    CarDetailsScreen(1, CarsViewModel())
}