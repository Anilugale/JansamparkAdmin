package com.vk.jansamparkadmin.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.ComplaintModel
import com.vk.jansamparkadmin.model.ComplaintReq
import com.vk.jansamparkadmin.model.VillageFilterReq
import com.vk.jansamparkadmin.ui.theme.*
import com.vk.jansamparkadmin.ui.viewmodel.ComplaintListViewModel
import com.vk.jansamparkadmin.ui.viewmodel.Status
import com.vk.jansamparkadmin.ui.viewmodel.VillageListViewModel

@Composable
fun ComplaintList(name: String, navigator: NavHostController) {

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row {
                    Text(
                        text = stringResource(id = R.string.complaint_list),
                        fontSize = 18.sp
                    )
                }

            },
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
            }
        )
    }

    ) {
        val vModel: ComplaintListViewModel = hiltViewModel()

        LaunchedEffect(key1 = true, block = {
            vModel.getTotalCount(ComplaintReq(village = name.replace("::","/"), fromdate = "", todate = ""))

        })
        val rememberVm = remember {
            vModel
        }
        val uiState = rememberVm.stateExpose.collectAsState().value

        Box(modifier = Modifier.padding(paddingValues = it)) {
            when (uiState) {

                is Status.Progress -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is Status.SuccessComplaintList -> {
                    ShowList(uiState.list, navigator, vModel)

                }

                is Status.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp),
                                contentDescription = ""
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "No comments available",
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No data Found",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ShowList(
    list: List<ComplaintModel>,
    navigatorController: NavHostController?,
    model: ComplaintListViewModel,
) {
    val lazyState = rememberLazyListState()

    LazyColumn(contentPadding = PaddingValues(5.dp), state = lazyState) {
        items(count = list.size, key = { it }) {
            ShowCommentItem(list[it], navigatorController)
        }
    }
}

@Composable
fun ShowCommentItem(model: ComplaintModel, navigatorController: NavHostController?) {
    val context = LocalContext.current
    Card(
        elevation = 2.dp, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clickable {
                navigatorController?.navigate("${Screens.ComplaintDetails.route}/${model.id}")
            }
    ) {

        val txtColor = if (isSystemInDarkTheme()) {
            FontColor1Dark
        } else {
            FontColor1
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            model.isurgent?.let {
                if (it == 1) {
                    Text(
                        text = "URGENT",
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color.Red, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .shadow(AppBarDefaults.TopAppBarElevation),
                        fontSize = 13.sp,

                        )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "BS-" + String.format("%06d", model.id),
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    fontSize = 12.sp,
                    color = FontColor2
                )

                Text(
                    text = model.ticket_date,
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    fontSize = 12.sp,
                    color = FontColor2
                )
            }

            Text(
                text = model.categorie_id,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                fontSize = 14.sp,
                color = txtColor
            )

            Text(
                text = model.ticket_exp,
                fontSize = 14.sp,
                color = txtColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp),
                textAlign = TextAlign.Start
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (!model.attachments.isNullOrEmpty()) {
                    Row(modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentWidth()
                        .clickable {
                            val url =
                                "https://shirdiyuva.in/uploads/complaints/${model.attachments}"
                            Log.d("@@", "ShowCommentItem: $url")
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                data = Uri.parse(url)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Attachment,
                            contentDescription = "",
                            tint = FontColor2,
                            modifier = Modifier
                                .height(20.dp)
                                .align(Alignment.CenterVertically),
                        )
                        Text(
                            text = "Attachment",
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                                .align(Alignment.CenterVertically),
                            fontSize = 12.sp,
                            color = FontColor2,
                            textAlign = TextAlign.Center
                        )


                    }
                }

                Text(
                    text = model.ticket_status,
                    modifier = Modifier
                        .background(getChipColor(model.ticket_status), RoundedCornerShape(5.dp))
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(5.dp),
                            color = FontColor2
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .wrapContentWidth()
                        .align(Alignment.CenterEnd),
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

    }
}

fun getChipColor(ticketStatus: String): Color {
    return when (ticketStatus.lowercase()) {
        "inprocess" -> {
            chipProgress
        }
        "rejected" -> {
            chipReject
        }
        else -> {
            chipClose
        }
    }
}