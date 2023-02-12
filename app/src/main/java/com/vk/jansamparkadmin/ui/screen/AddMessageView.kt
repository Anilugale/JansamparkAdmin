package com.vk.jansamparkadmin.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.Admin
import com.vk.jansamparkadmin.model.CoordinateModel
import com.vk.jansamparkadmin.model.MessageReqModel
import com.vk.jansamparkadmin.ui.theme.FontColor2
import com.vk.jansamparkadmin.ui.viewmodel.AddMessageState
import com.vk.jansamparkadmin.ui.viewmodel.AddMessageViewModel

@Composable
fun AddMessageView(navigator: NavHostController) {
    val isProgressBar = remember {
        mutableStateOf(false)
    }

    val current = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.add_msg)) },
                navigationIcon = if (navigator?.previousBackStackEntry != null) {
                    {
                        IconButton(onClick = { navigator.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else {
                    null
                })
        },
        modifier = Modifier.background(color = MaterialTheme.colors.background),
    ) {


        val vModel: AddMessageViewModel = hiltViewModel()
        val rememberVm = remember {
            vModel
        }

        when (val uiState = rememberVm.stateExpose.collectAsState().value) {
            AddMessageState.Empty -> {

            }

            AddMessageState.Progress -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is AddMessageState.SuccessCoList -> {
                ShowAddMsg(uiState.list,vModel,isProgressBar)
            }

            else -> {}
        }
        when (val it = rememberVm.stateExpose1.collectAsState().value) {

            AddMessageState.Progress -> {
                isProgressBar.value = true
            }

            is AddMessageState.SuccessMSG -> {
                isProgressBar.value = false
                it.msg.apply {
                    current.toast(this)
                }
                Cache.msgList.clear()
                navigator?.navigateUp()

            }
            is AddMessageState.ERROR ->{
                it.msg?.apply {
                    current.toast(this)
                }

            }
            else -> {}
        }
    }



    if(isProgressBar.value){
        ShowProgressDialog {

        }
    }

}

@Composable
fun ShowAddMsg(
    list: List<CoordinateModel>?,
    vModel: AddMessageViewModel?,
    value: MutableState<Boolean>?
) {
    val selectedComplaintStatus = remember {
        mutableStateOf<CoordinateModel?>(null)
    }
    val sharedPreferences = LocalContext.current.getSharedPreferences(
        stringResource(id = R.string.app_name),
        Context.MODE_PRIVATE
    )
    val string = sharedPreferences.getString("user", null)

    val user = if(string!=null){
        try {
            val admin = Gson().fromJson(string, Admin::class.java)
            admin.name
        }catch (e:Exception){
            e.printStackTrace()
            ""
        }
    }else{
        ""
    }
    val messageText = remember {
        mutableStateOf("")
    }
    Column {

        DropDownSpinnerCoordinate(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .border(
                    shape = RoundedCornerShape(8.dp),
                    width = 1.dp, color =
                    FontColor2
                ),
            defaultText = "समन्वयक निवडा",
            selectedItem = selectedComplaintStatus.value,
            onItemSelected = { _, item ->
                selectedComplaintStatus.value = item
            },
            itemList = list!!
        )
        OutlinedTextField(
            value = messageText.value,
            onValueChange = {
                messageText.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp)
                .padding(vertical = 15.dp, horizontal = 10.dp),
            label = { Text(text = "Enter Message here ...") }
        )

        Button(
            onClick = {
                vModel?.sendMessage(MessageReqModel(
                    coordinator_id = selectedComplaintStatus.value!!.id.toString(),
                    device_token =selectedComplaintStatus.value!!.deviceId,
                    message = messageText.value,
                    from = user
                ))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 10.dp),
            enabled = messageText.value.isNotEmpty() && selectedComplaintStatus.value!=null,
        ) {
            Text(text = "Submit")
        }
    }


}
