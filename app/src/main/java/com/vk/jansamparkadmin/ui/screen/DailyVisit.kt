package com.vk.jansamparkadmin.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.jansamparkadmin.BuildConfig
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.*
import com.vk.jansamparkadmin.ui.theme.FontColor1
import com.vk.jansamparkadmin.ui.theme.FontColor1Dark
import com.vk.jansamparkadmin.ui.theme.FontColor2
import com.vk.jansamparkadmin.ui.theme.Teal200
import com.vk.jansamparkadmin.ui.viewmodel.DailVisitVM
import com.vk.jansamparkadmin.ui.viewmodel.DailyVisitState

@Composable
fun DailyVisit(navigator: NavHostController?) {
    val vmModel: DailVisitVM = hiltViewModel()
    val viewModel = remember {
        vmModel
    }
    Scaffold(
        topBar = { TopAppBar(title = {
            Column {
                Text(text = stringResource(id = R.string.daily_visit))
                Text(text = "       v ${BuildConfig.VERSION_NAME}", fontSize = 10.sp)
            }
        }) },
        bottomBar = { BottomNavigationBar(navController = navigator!!) },
    ) { padding ->


        when (val uiState = viewModel.stateExpose.collectAsState().value) {
            DailyVisitState.Empty -> {

            }

            DailyVisitState.Progress -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DailyVisitState.SuccessCoList -> {
                ShowDailyVisit(uiState.list, viewModel, padding)
            }
            is DailyVisitState.ERROR->{
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    Text(text = uiState.msg?:"", fontWeight = FontWeight.SemiBold)
                }
            }

            else -> {}
        }
    }
}

@Composable
fun ShowDailyVisit(list: List<CoordinateModel>?, viewModel: DailVisitVM, padding: PaddingValues) {
    val selCoordinator = remember {
        mutableStateOf<CoordinateModel?>(null)
    }
    val fromDialog = remember { mutableStateOf(false) }
    val toDialog = remember { mutableStateOf(false) }
    val fromDate = remember { mutableStateOf("") }
    val toDate = remember { mutableStateOf("") }
    val fromDateCompare = remember { mutableStateOf("") }
    val toDateCompare = remember { mutableStateOf("") }
    val current = LocalContext.current
    Column(modifier = Modifier.padding(padding)) {
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
            selectedItem = selCoordinator.value,
            onItemSelected = { _, item ->
                selCoordinator.value = item
            },
            itemList = list!!
        )
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            OutlinedButton(
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(color = FontColor2, width = 1.dp),
                modifier = Modifier
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .wrapContentWidth()
                    .weight(3f),
                onClick = {
                    fromDialog.value = true
                }) {

                Icon(Icons.Outlined.CalendarToday, contentDescription = "")
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = fromDate.value.ifEmpty {
                        "From date"
                    },
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body1,
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(color = FontColor2, width = 1.dp),
                modifier = Modifier
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .wrapContentWidth()
                    .weight(3f),
                onClick = {
                    toDialog.value = true
                }) {

                Icon(Icons.Outlined.CalendarToday, contentDescription = "")
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = toDate.value.ifEmpty {
                        "To date"
                    },
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body1,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    if(toDateCompare.value >= fromDateCompare.value){
                        viewModel.getDailyVisits(
                            DailyVisitReq(
                                coordinatorid = selCoordinator.value?.id.toString(),
                                fromdate = fromDate.value,
                                todate = toDate.value
                            )
                        )
                    }else{
                        current.toast("To Date not greater than From date ")
                    }

                }, modifier = Modifier
                    .weight(2f)
                    .align(CenterVertically),
                enabled = fromDate.value.isNotEmpty() &&
                        toDate.value.isNotEmpty() &&
                        selCoordinator.value != null
            )
            {
                Text(text = "Search")
            }
        }


        when (val listValue = viewModel.stateExpose1.collectAsState().value) {
            is DailyVisitState.Progress->{
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    CircularProgressIndicator()
                }
            }
            is DailyVisitState.SuccessDailyList -> {
                if (listValue.list.isNotEmpty()) {
                    ShowDailyList(listValue.list)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                        Text(text = "No daily visited found", fontWeight = FontWeight.SemiBold)
                    }
                }

            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    Text(
                        text = "Select Coordinator and date to search ",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

    }

    if (fromDialog.value) {
        rememberDatePicker(
            { date, time ->
                fromDate.value = date
                fromDateCompare.value = time
                fromDialog.value = false
            },
        ) {
            fromDialog.value = false
        }
    }

    if (toDialog.value) {
        rememberDatePicker(
            { date, time ->
                toDate.value = date
                toDateCompare.value = time
                toDialog.value = false
            },
        ) {
            toDialog.value = false
        }
    }
}

@Composable
fun ShowDailyList(list: List<DailyVisitDayModel>) {
    val isShowDetails = remember {
        mutableStateOf(false)
    }
    val selectedModel = remember {
        mutableStateOf<DailyVisitModel?>(null)
    }
    LazyColumn {
        list.forEach {
            item {
                Text(text = it.date, modifier = Modifier.padding(10.dp))
            }
            it.list.forEach { model ->
                item {
                    Card(modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            selectedModel.value = model
                            isShowDetails.value = true
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            Text(
                                text = model.villagename.toString(),
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(
                                text = model.createddate ?: "",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
    if (isShowDetails.value && selectedModel.value != null) {
        ShowDailyDetails(selectedModel.value!!) {
            isShowDetails.value = !isShowDetails.value
        }
    }
}


@Composable
fun ShowDailyDetails(dailyModel: DailyVisitModel, function: () -> Unit) {
    Dialog(
        onDismissRequest = function,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(10.dp)
            ) {
                val titleColor = if (isSystemInDarkTheme()) FontColor1Dark else FontColor1
                if (dailyModel.persons_visited.isNotEmpty()) {
                    Text(
                        text = "Person Visited ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 16.sp
                    )
                    PersonVisitedDetailsUI(titleColor, dailyModel.persons_visited)

                }

                ShowInfo(
                    dailyModel.devinfo,
                    R.string.development_info,
                    dailyModel.devinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.rashanshopinfo,
                    R.string.ration_info,
                    dailyModel.rashanshopinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.electricityinfo,
                    R.string.electric_info,
                    dailyModel.electricityinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.drinkingwaterinfo,
                    R.string.drinking_water_info,
                    dailyModel.drinkingwaterinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.watercanelinfo,
                    R.string.water_canal_info,
                    dailyModel.watercanelinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.schoolinfo,
                    R.string.school_info,
                    dailyModel.schoolinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.primarycarecenterinfo,
                    R.string.prathamik_info,
                    dailyModel.primarycarecenterinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.veterinarymedicineinfo,
                    R.string.pashu_info,
                    dailyModel.veterinarymedicineinfoinfo,
                    titleColor
                )
                ShowInfo(
                    dailyModel.govservantinfo,
                    R.string.gov_emp_info,
                    dailyModel.govservantinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.politicalinfo,
                    R.string.politics_info,
                    dailyModel.politicalinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.deathpersoninfo,
                    R.string.death_person_info,
                    dailyModel.deathpersoninfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.birthdayinfo,
                    R.string.birthday_info,
                    dailyModel.birthdayinfofile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.newschemes,
                    R.string.gat_labh_yojna,
                    dailyModel.newschemesfile,
                    titleColor
                )
                ShowInfo(
                    dailyModel.otherinfo,
                    R.string.other_info,
                    dailyModel.otherinfofile,
                    titleColor
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

@Composable
fun PersonVisitedDetailsUI(titleColor: Color, personsVisited: List<PersonsVisited>) {
    personsVisited.forEach {
        Card(modifier = Modifier.padding(vertical = 10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                if (it.name.isNotEmpty()) {
                    Text(
                        text = "नाव - ${it.name} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
                if (it.subject.isNotEmpty()) {
                    Text(
                        text = "विषय - ${it.subject} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
                if (it.information.isNotEmpty()) {
                    Text(
                        text = "माहिती - ${it.information} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
                if (it.servey.isNotEmpty()) {
                    Text(
                        text = "सर्वेक्षण - ${it.servey} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowInfo(
    msg: String?,
    placeholderID: Int,
    attachmentUrl: String?,
    titleColor: Color
) {
    val current = LocalContext.current

    if (!msg.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = placeholderID),
            fontWeight = FontWeight.SemiBold,
            color = FontColor2,
            fontSize = 14.sp
        )

        Text(
            text = msg,
            color = titleColor,
            fontSize = 16.sp
        )

        if (attachmentUrl != null && attachmentUrl.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .clickable {
                        val url = "https://shirdiyuva.in/${attachmentUrl}"
                        Log.d("@@", "ShowCommentItem: $url")
                        val intent = Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse(url)
                        }
                        current.startActivity(intent)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Attachment,
                    contentDescription = "attachment",
                    tint = FontColor2
                )
                Text(
                    text = "Attachment", modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .align(CenterVertically),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = FontColor2
                )
            }
        }


    }

}