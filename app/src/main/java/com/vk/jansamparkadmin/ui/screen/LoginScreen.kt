package com.vk.jansamparkadmin.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.LoginReqModel
import com.vk.jansamparkadmin.ui.theme.FontColor1
import com.vk.jansamparkadmin.ui.theme.FontColor1Dark
import com.vk.jansamparkadmin.ui.theme.FontColor2
import com.vk.jansamparkadmin.ui.viewmodel.LoginViewModel
import com.vk.jansamparkadmin.ui.viewmodel.Status

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navigator: NavHostController?) {
    val model: LoginViewModel = hiltViewModel()
    val remeberModel = remember {
        model
    }
    val value = remeberModel.stateExpose.collectAsState().value
    val keyboardController = LocalSoftwareKeyboardController.current


    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val userName = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }

    val isProcess = remember {
        mutableStateOf(model.isProcess)
    }

    when (value) {
        Status.Empty -> {
            LoginUI(userName, isProcess, password, passwordVisible, model)
        }
        is Status.ErrorLogin -> {
            isProcess.value = false
            Toast.makeText(LocalContext.current, value.msg, Toast.LENGTH_SHORT).show()
            userName.value = ""
            password.value = ""

            LoginUI(userName, isProcess, password, passwordVisible, model)
        }
        Status.Progress -> {
            keyboardController?.hide()

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Status.SuccessUser -> {
            val sharedPreferences = LocalContext.current.getSharedPreferences(
                stringResource(id = R.string.app_name),
                Context.MODE_PRIVATE
            )
            LaunchedEffect(key1 = value) {
                if (value.user.size == 1) {
                    sharedPreferences.edit().putBoolean("isLogin", true).apply()
                    sharedPreferences.edit().putString("user", Gson().toJson(value.user[0])).apply()
                    navigator?.navigate(Screens.Dashboard.route)
                }
            }
        }
    }


}

@Composable
fun LoginUI(
    userName: MutableState<String>,
    isProcess: MutableState<Boolean>,
    password: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    model: LoginViewModel
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val scrollState = rememberScrollState()

        val FontColor = if (isSystemInDarkTheme()) {
            FontColor1Dark
        } else {
            FontColor1
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight()
                .scrollable(scrollState, Orientation.Vertical)
        ) {

            Text(
                text = "Login", fontSize = 24.sp,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                color = FontColor
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                value = userName.value,
                onValueChange = { userName.value = it },
                label = { Text(text = "Username", color = FontColor2) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.textFieldColors(textColor = FontColor),


                )

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = "Password", color = FontColor2) },
                singleLine = true,
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description =
                        if (passwordVisible.value) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.textFieldColors(textColor = FontColor),
                textStyle = MaterialTheme.typography.overline
            )



            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    model.login(LoginReqModel(userName.value, password.value))
                },

                enabled = ((userName.value.isNotEmpty() && password.value.isNotEmpty()) || isProcess.value)
            ) {
                Text(text = "Login")
            }

            if (isProcess.value) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }
    }
}


@Composable
@Preview
fun preLogin() {
    LoginScreen(null)
}