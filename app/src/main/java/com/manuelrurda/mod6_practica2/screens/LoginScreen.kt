package com.manuelrurda.mod6_practica2.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.manuelrurda.mod6_practica2.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val activity = LocalActivity.current

    val webClientId = stringResource(R.string.default_web_client_id)
    val emptyFieldsError = stringResource(R.string.empty_fields_error)
    val loginSuccess = stringResource(R.string.login_success)
    val accountCreated = stringResource(R.string.account_created)

    val auth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Deberia user viewModel para guardar esto pero por motivos de tiempo aqui ira :)

    val screenState = remember { mutableStateOf("Login") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }

    val googleSignInLauncher = rememberFirebaseAuthLauncher(
        auth = auth,
        onAuthComplete = { result ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Google sign-in successful",
                    duration = SnackbarDuration.Short
                )
            }
            onLoginSuccess()
        },
        onAuthError = { error ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Google sign-in failed: ${error.localizedMessage}",
                    duration = SnackbarDuration.Long
                )
            }
        }
    )

    LaunchedEffect(auth.currentUser) {
        if(auth.currentUser != null){
            onLoginSuccess()
        }
    }

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


                Text(text = stringResource(if (screenState.value == "Login") R.string.login_title else R.string.create_account_title),
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

                Text(stringResource(R.string.password_hint), modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.password_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(
                    R.string.forgot_password),
                    modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                        .clickable {
                            onForgotPasswordClick()
                        },
                    textAlign = TextAlign.End
                )

                Text(
                    stringResource(
                    if (screenState.value == "Login") R.string.no_account_prompt else R.string.have_account_prompt),
                    modifier = Modifier
                    .clickable {
                        screenState.value = if (screenState.value == "Login") "Create Account" else "Login"
                        email.value = ""
                        password.value = ""
                    }
                    .padding(top = 10.dp))

                Button(onClick = {
                    if (email.value.isEmpty() || password.value.isEmpty()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = emptyFieldsError,
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@Button
                    }
                    coroutineScope.launch {
                        isLoading.value = true
                        try {
                            if (screenState.value == "Login") {
                                auth.signInWithEmailAndPassword(email.value, password.value).await()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = loginSuccess,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                auth.createUserWithEmailAndPassword(email.value, password.value).await()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = accountCreated,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                            onLoginSuccess()
                        } catch (e: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${e.localizedMessage}",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        } finally {
                            isLoading.value = false
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)) {
                    Text(text = stringResource(if (screenState.value == "Login") R.string.login_button else R.string.create_account_button))
                }

                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )

                    Text(
                        stringResource(R.string.or_sign_in_with),
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
                Button(onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(webClientId)
                        .requestEmail()
                        .build()
                    val googleSignInClient = activity?.let { it1 -> GoogleSignIn.getClient(it1, gso) }
                    if (googleSignInClient != null) {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)) {
                    Text(stringResource(R.string.google_sign_in_button))
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

@Composable
fun rememberFirebaseAuthLauncher(
    auth: FirebaseAuth,
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (Exception) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val activity = LocalActivity.current as Activity
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            onAuthComplete(authTask.result!!)
                        } else {
                            onAuthError(authTask.exception!!)
                        }
                    }
            }
        } catch (e: Exception) {
            onAuthError(e)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    LoginScreen()
}