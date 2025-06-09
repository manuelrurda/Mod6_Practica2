package com.manuelrurda.mod6_practica2.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.manuelrurda.mod6_practica2.R
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ForgotPasswordScreen(
    onForgotPasswordSuccess: () -> Unit = {}
) {

    val email = remember { mutableStateOf("") }
    val emptyEmailError = stringResource(R.string.empty_email_error)
    val emailSentMessage = stringResource(R.string.email_sent_message)

    val isLoading = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        )
        {
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize())
            {


                Text(text = stringResource(R.string.recover_pw_title),
                    style = TextStyle(fontSize = 26.sp),
                    modifier = Modifier.padding(top = 50.dp)
                )

                Text(stringResource(R.string.email_hint), modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.email_hint)) },
                    modifier = Modifier.fillMaxWidth())

                Button(onClick = {
                    if (email.value.isNotBlank()) {
                        Firebase.auth.sendPasswordResetEmail(email.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = emailSentMessage,
                                            duration = SnackbarDuration.Long
                                        )
                                    }
                                    onForgotPasswordSuccess()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Error: ${task.exception?.message}",
                                            duration = SnackbarDuration.Long
                                        )
                                    }
                                }
                            }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = emptyEmailError,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)) {
                    Text(text = stringResource(R.string.send_email))
                }
            }
        }

        if(isLoading.value){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )

                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}