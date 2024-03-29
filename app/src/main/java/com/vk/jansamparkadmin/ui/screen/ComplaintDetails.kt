package com.vk.jansamparkadmin.ui.screen


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.Admin
import com.vk.jansamparkadmin.model.CloseComplaintReq
import com.vk.jansamparkadmin.model.Comment
import com.vk.jansamparkadmin.ui.theme.*
import com.vk.jansamparkadmin.ui.viewmodel.ComplaintDetailsVM
import com.vk.jansamparkadmin.ui.viewmodel.DetailsState
import com.vk.jansamparkadmin.ui.viewmodel.MarkImportant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComplaintDetails(id: String, navigatorController: NavHostController?) {

    val comment = Cache.getComplaintFromID(id.toInt())
    if (comment == null) {
        navigatorController?.popBackStack()
    }

    val model: ComplaintDetailsVM = hiltViewModel()
    val viewModel = remember { mutableStateOf(model) }
    val value1 = viewModel.value.stateExpose.collectAsState().value
    val context = LocalContext.current
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
    LaunchedEffect(key1 = true) {
        model.getList(isProgress = true, comment = comment)
    }
    LaunchedEffect(key1 = comment) {
        model.getList(comment = comment)
    }


    val listState = rememberLazyListState()


    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            it != ModalBottomSheetValue.HalfExpanded
        }
    )
    val clickID = remember {
        mutableStateOf("0")
    }
    val coroutineScope = rememberCoroutineScope()

    val showProgressDailog = remember {
        mutableStateOf(false)
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            text = LocalContext.current.getString(R.string.complaint_details),
                            fontSize = 18.sp
                        )
                    }

                },
                navigationIcon = if (navigatorController?.previousBackStackEntry != null) {
                    {
                        IconButton(onClick = { navigatorController.navigateUp() }) {
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
        },
        backgroundColor = MaterialTheme.colors.background
    ) {
        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContent = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)

                        .background(
                            if (isSystemInDarkTheme()) {
                                StickyHeaderDark
                            } else (StickyHeaderLight),

                            )
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Menu", modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            color = FontColor2,
                            textAlign = TextAlign.Center
                        )

                    }
                }
            },
            sheetBackgroundColor = Color.Transparent,
            scrimColor = Color.Unspecified
        ) {
            LazyColumn(state = listState) {
                item(key = "header") {
                    Column {
                        ShowCommentItem(comment!!, null)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if(comment.isurgent !=1){
                                OutlinedButton(
                                    onClick = {
                                        model.markThisImportant(comment)
                                        showProgressDailog.value = true
                                    },
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Outlined.PriorityHigh, contentDescription = "")
                                    Text(
                                        text = "Important",
                                        color = MaterialTheme.colors.primary,
                                        fontSize = 13.sp,
                                    )
                                }
                            }

                            if(comment.ticket_status != "Closed"){
                                OutlinedButton(
                                    onClick = {
                                        model.closeComplaint(CloseComplaintReq(
                                            modifiedby = user,
                                            ticket_id = comment.id.toString()
                                        ),comment)
                                        showProgressDailog.value = true
                                    },
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Closed Complaint/बंद तक्रार",
                                        color = MaterialTheme.colors.primary,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                        }

                    }
                }
                when (value1) {

                    is DetailsState.Process -> {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator()
                            }
                        }

                    }

                    is DetailsState.SuccessDelete -> {
                        Toast.makeText(context, value1.msg, Toast.LENGTH_SHORT).show()
                        items(value1.commentList.size, key = { it }) {
                            CommentItem(
                                model = value1.commentList[it],
                                clickID = clickID,
                                modalBottomSheetState,
                                coroutineScope
                            )
                        }
                    }

                    is DetailsState.FailedDelete -> {
                        Toast.makeText(context, value1.msg, Toast.LENGTH_SHORT).show()
                    }

                    is DetailsState.Success -> {
                        items(value1.commentList.size, key = { it }) {
                            val color = if (isSystemInDarkTheme()) {
                                FontColor1Dark
                            } else {
                                FontColor1
                            }
                            Text(
                                text = value1.commentList[it],
                                color = color,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }


                    else -> {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(text = "No list available")
                            }
                        }

                    }
                }

            }
        }

        val showAlertDailog = remember {
            mutableStateOf(false)
        }

        val showAlertDailogMsg= remember {
            mutableStateOf("")
        }

        when (val state = viewModel.value.stateExposeMark.collectAsState().value) {
            MarkImportant.Empty -> {

            }
            is MarkImportant.Failed -> {
                LaunchedEffect(key1 = state) {
                    showProgressDailog.value = false
                    showAlertDailogMsg.value = state.msg
                    showAlertDailog.value = true
                }
            }
            is MarkImportant.Success -> {
                LaunchedEffect(key1 = state) {
                    showAlertDailogMsg.value = state.msg
                    showProgressDailog.value = false
                    showAlertDailog.value = true
                }
            }
        }

        if (showProgressDailog.value) {
            ShowProgressDialog {
                showProgressDailog.value = false
            }
        }

        if (showAlertDailog.value) {
            ShowAlertDialog(showAlertDailogMsg.value) {
                showAlertDailog.value = false
            }
        }

    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentItem(
    model: Comment,
    clickID: MutableState<String>,
    modalBottomSheetState: ModalBottomSheetState?,
    coroutineScope: CoroutineScope,
) {

    val color = if (isSystemInDarkTheme()) {
        FontColor1Dark
    } else {
        FontColor1
    }


    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .fillMaxWidth()
        ) {


            Text(
                text = model.comment,
                color = color,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 3.dp, end = 15.dp)
                    .fillMaxWidth()
            )


            Text(
                text = if (model.updated_at != null) {
                    "Updated on ${model.updated_at.substring(0, model.updated_at.lastIndexOf(":"))}"
                } else {
                    "Created on ${
                        model.comment_date.substring(
                            0,
                            model.comment_date.lastIndexOf(":")
                        )
                    }"
                },
                color = FontColor2,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 3.dp)
                    .fillMaxWidth()
            )


            model.comment_attachment?.apply {
                if (this.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterStart)
                                .clickable {
                                    val url =
                                        "https://shirdiyuva.in/uploads/complaints/${model.comment_attachment}"
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
                                    .height(23.dp)
                                    .align(CenterVertically)
                                    .padding(5.dp),
                            )
                            Text(
                                text = "Attachment",
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                                    .align(CenterVertically),
                                fontSize = 12.sp,
                                color = FontColor2,
                                textAlign = TextAlign.Center
                            )


                        }

                    }


                }
            }


        }

        Icon(
            Icons.Outlined.MoreVert,
            contentDescription = "",
            tint = color,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .padding(10.dp)
                .align(TopEnd)
                .clickable {
                    clickID.value = model.comment_id.toString()
                    coroutineScope.launch {
                        if (modalBottomSheetState != null) {
                            if (modalBottomSheetState.isVisible) {
                                modalBottomSheetState.hide()
                            } else {
                                modalBottomSheetState.show()
                            }
                        }

                    }
                }

        )

        Divider(modifier = Modifier.align(Alignment.BottomCenter))
    }

}


@Composable
@Preview
fun PreviewDetails() {
    ComplaintDetails(id = "d", navigatorController = null)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun CommentItemPreview() {
    CommentItem(
        model = Comment(
            comment = "comment text for ui showing here comment text for ui showing here",
            comment_id = 1,
            comment_attachment = "comment Attachment",
            comment_date = "12/12/2012 12:00:00",
            updated_at = "12/12/2012 12:00:00"
        ),
        clickID = mutableStateOf("0"),
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = null
    )
}
