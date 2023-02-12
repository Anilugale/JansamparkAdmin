package com.vk.jansamparkadmin.ui.screen

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.Admin
import com.vk.jansamparkadmin.model.MessageListReqModel
import com.vk.jansamparkadmin.model.MessageModel
import com.vk.jansamparkadmin.ui.theme.Teal200
import com.vk.jansamparkadmin.ui.viewmodel.MessageViewModel
import com.vk.jansamparkadmin.ui.viewmodel.MsgListStatus
import java.text.SimpleDateFormat

@Composable
fun MessageListView(navigator: NavHostController) {
    val sharedPreferences = LocalContext.current.getSharedPreferences(
        stringResource(id = R.string.app_name),
        Context.MODE_PRIVATE
    )
    val string = sharedPreferences.getString("user", null)

    val user = if(string!=null){
        try {
            val admin = Gson().fromJson(string, Admin::class.java)
            if (admin.name.equals("Admin",true)) {
                ""
            }else{
                admin.name
            }
        }catch (e:Exception){
            e.printStackTrace()
            ""
        }
    }else{
        ""
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.msg_list)) }) },
        bottomBar = { BottomNavigationBar(navController = navigator) },
        modifier = Modifier.background(color = MaterialTheme.colors.background),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigator.navigate(Screens.AddMessage.route)
            }) {
                Icon(Icons.Outlined.AddCircle, contentDescription = "")
            }
        }
    ) {


        val vModel: MessageViewModel = hiltViewModel()
        val rememberVm = remember {
            vModel
        }
        LaunchedEffect(key1 = Cache.msgList){
            vModel.getVillageList(MessageListReqModel(admin_name = user))
        }

        when (val uiState = rememberVm.stateExpose.collectAsState().value) {
            MsgListStatus.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Text(text = "No message Found")
                }
            }

            MsgListStatus.Progress -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MsgListStatus.SuccessMsgList -> {
                ShowMessageList(uiState.msgList.reversed(),it)
            }

            else -> {}
        }
    }


}

@Composable
fun ShowMessageList(msglist: List<MessageModel>, paddingValues: PaddingValues) {
    val isShowMessageDialog = remember {
        mutableStateOf(false)
    }

    val currentMessageShow = remember {
        mutableStateOf<MessageModel?>(null)
    }

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(paddingValues)) {
        items(count = msglist.size, key = { it }) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .fillMaxWidth()
                    .clickable {
                        isShowMessageDialog.value = true
                        currentMessageShow.value = msglist[it]
                    }
            ) {
                msglist[it].apply {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row {
                            Text(text = "To:", fontSize = 14.sp, modifier = Modifier.align(CenterVertically))
                            Text(
                                text = name ?: "Unknown", fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 5.dp)
                                    .align(CenterVertically)
                                    .background(
                                        color = Teal200.copy(0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 3.dp)
                            )

                        }
                        Row {
                            Text(text = "From:", fontSize = 14.sp, modifier = Modifier.align(CenterVertically))
                            Text(
                                text = admin_name ?: "Admin", fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 5.dp)
                                    .align(CenterVertically)
                                    .background(
                                        color = Teal200.copy(0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 3.dp)
                            )

                        }
                        Text(text = message, overflow = TextOverflow.Ellipsis, maxLines = 4)
                        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = try {
                            fmt.parse(createddate)?.let { it1 ->
                                DateUtils.getRelativeTimeSpanString(
                                    it1.time,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS
                                )
                            }
                        } catch (e: Exception) {
                           createddate
                        }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = date.toString(),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }

                    }
                }

            }
        }
    }

    if (isShowMessageDialog.value) {
        ShowMessageDialog(msg = currentMessageShow.value) {
            isShowMessageDialog.value = !isShowMessageDialog.value
        }
    }
}


@Composable
fun ShowMessageDialog(msg: MessageModel?, function: () -> Unit) {
    msg?.apply {
        val color = if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
        Dialog(
            onDismissRequest = function,
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(10.dp)
                ) {
                    Row {
                        Text(text = "To:", color = color, fontSize = 14.sp, modifier = Modifier.align(CenterVertically))
                        Text(
                            text = name ?: "Unknown", color = color, fontSize = 14.sp,
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 5.dp)
                                .align(CenterVertically)
                                .background(
                                    color = Teal200.copy(0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 3.dp)
                        )



                        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = try {
                            fmt.parse(createddate)?.let { it1 ->
                                DateUtils.getRelativeTimeSpanString(
                                    it1.time,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS
                                )
                            }
                        } catch (e: Exception) {
                            createddate
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max) ){
                            Text(
                                text = date.toString(),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }


                    }

                    Row{
                        Text(text = "From:", color = color, fontSize = 14.sp, modifier = Modifier.align(CenterVertically))
                        Text(
                            text = admin_name ?: "Admin", color = color, fontSize = 14.sp,
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 5.dp)
                                .align(CenterVertically)
                                .background(
                                    color = Teal200.copy(0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 3.dp)
                        )
                    }


                    Text(
                       modifier = Modifier.padding(start = 10.dp),
                        text = message,
                        color = color,
                        fontSize = 17.sp
                    )



                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Okay",
                            fontSize = 16.sp,
                            color = Teal200,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    function()
                                }
                        )
                    }
                }

            },
        )
    }

}
