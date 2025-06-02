package com.manuelrurda.mod6_practica2.screens

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun CarListScreen(viewModel: CarsViewModel, onCarClick: (Int) -> Unit) {
    val carsUiState by viewModel.carsUiState.collectAsState()
    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    var soundId by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        soundId = soundPool.load(context, R.raw.button, 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when(carsUiState){
            is UiState.Loading -> {
                LoadingAnimation(
                    circleColor = Color.Black,
                    circleSize = 20.dp,
                    width = 110.dp
                )
            }
            is UiState.Success -> {
                CarListColumn(
                    data = (carsUiState as UiState.Success).data,
                    onCarClick = {
                        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                        onCarClick(it)
                    }
                )
            }
            is UiState.Error -> {
                val message = when(carsUiState as UiState.Error){
                    is UiState.Error.ServerError -> stringResource(id = R.string.error_server_error)
                    is UiState.Error.NetworkError -> stringResource(id = R.string.error_network_error)
                    is UiState.Error.UnexpectedError -> stringResource(id = R.string.error_unexpected_error)
                }
                ErrorCard(
                    message = message,
                    onTap = {
                        viewModel.getCars()
                    }
                )
            }
        }
    }

}

@Composable
fun CarListColumn(data: List<CarDto>, onCarClick: (Int) -> Unit){
    LazyColumn(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        items(data){
            CarListItem(it, onCarClick)
        }
    }
}

@Composable
fun CarListItem(carModel: CarDto, onCarClick: (Int) -> Unit) {
    val hasImage = carModel.imageUrl.isNotEmpty()
    val img = rememberImagePainter(data = carModel.imageUrl)
    ElevatedCard(
        modifier = Modifier
            .padding(all = 8.dp)
            .clickable {
                onCarClick(carModel.id)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Image(
                painter = if (hasImage) img
                else painterResource(R.drawable.car_placeholder),
                contentDescription = stringResource(id = R.string.description_car),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        shape = CircleShape,
                        color = Color.White
                    )
                    .background(color = Color.White)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = carModel.make, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                Text(text = "${carModel.year} • ${carModel.model}", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Preview
@Composable
private fun CarListScreenPreview() {
    CarListScreen(CarsViewModel(), {})
}