package com.vk.jansamparkadmin.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.CategoryModel
import com.vk.jansamparkadmin.model.Village
import com.vk.jansamparkadmin.model.VillageCount
import com.vk.jansamparkadmin.model.VillageFilterReq
import com.vk.jansamparkadmin.ui.theme.FontColor2
import com.vk.jansamparkadmin.ui.theme.Purple80
import com.vk.jansamparkadmin.ui.viewmodel.Status
import com.vk.jansamparkadmin.ui.viewmodel.VillageListViewModel


@Composable
fun VillageList(navigator: NavHostController?) {
    val isShowFilterDialog = remember {
        mutableStateOf(false)
    }

    val fromDate = remember { mutableStateOf("") }
    val fromDateCompare = remember { mutableStateOf("") }
    val toDate = remember { mutableStateOf("") }
    val toDateCompare = remember { mutableStateOf("") }

    val vModel: VillageListViewModel = hiltViewModel()
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Dashboard") }) },
        bottomBar = { BottomNavigationBar(navController = navigator!!) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isShowFilterDialog.value = !isShowFilterDialog.value
            }) {
                Icon(Icons.Outlined.FilterAlt, contentDescription = "")
            }
        }
    ) {

        val rememberVm = remember {
            vModel
        }
        val uiState = rememberVm.stateExpose.collectAsState().value
        Column {


            when (uiState) {
                Status.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No data found")
                    }
                }

                is Status.ErrorLogin -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.msg)
                    }
                }
                Status.Progress -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Status.SuccessVillage -> {
                    VillageCountListUI(uiState.list, navigator)
                }
                is Status.SuccessUser -> TODO()
                else -> {}
            }
        }
    }
    val current = LocalContext.current
    val villageName = remember { mutableStateOf<Village?>(null) }
    val gatName = remember { mutableStateOf<Village?>(null) }
    val selectedComplaint = remember { mutableStateOf<CategoryModel?>(null) }
    val selectedComplaintStatus = remember { mutableStateOf("") }
    if (isShowFilterDialog.value) {
        ShowPointDialog(
            fromDate,
            toDate,
            fromDateCompare,
            toDateCompare,
            villageName,
            gatName,
            selectedComplaint,
            selectedComplaintStatus,
            onDismiss = {
                isShowFilterDialog.value = !isShowFilterDialog.value
                if (fromDate.value.isNotEmpty() && toDate.value.isNotEmpty()) {
                    if ((fromDateCompare.value.compareTo(toDateCompare.value)) > 0) {
                        current.toast("From Date not less than To date")
                    }else{
                        vModel.getTotalCount(
                            VillageFilterReq(
                                fromdate = fromDate.value,
                                todate = toDate.value,
                                gat_name = if (gatName.value != null) {
                                    gatName.value!!.gat
                                } else {
                                    ""
                                },
                                village_id = if (villageName.value != null) {
                                    villageName.value!!.id.toString()
                                } else {
                                    ""
                                },
                                complaint_id = if (selectedComplaint.value!=null) {
                                    selectedComplaint.value!!.id
                                }else{
                                    ""
                                },
                                complaint_state = selectedComplaintStatus.value.lowercase()
                            )
                        )
                    }
                }else{
                    vModel.getTotalCount(
                        VillageFilterReq(
                            fromdate = fromDate.value,
                            todate = toDate.value,
                            gat_name = if (gatName.value != null) {
                                gatName.value!!.gat
                            } else {
                                ""
                            },
                            village_id = if (villageName.value != null) {
                                villageName.value!!.id.toString()
                            } else {
                                ""
                            },
                            complaint_id = if (selectedComplaint.value!=null) {
                                selectedComplaint.value!!.id
                            }else{
                                ""
                            },
                            complaint_state = selectedComplaintStatus.value.lowercase()
                        )
                    )
                }

            },
            onClear = {
                isShowFilterDialog.value = !isShowFilterDialog.value
                fromDate.value = ""
                toDate.value = ""
                fromDateCompare.value = ""
                toDateCompare.value = ""
                villageName.value = null
                gatName.value = null
                selectedComplaint.value = null
                selectedComplaintStatus.value = ""
                vModel.getTotalCount(
                    VillageFilterReq(
                        fromdate = fromDate.value,
                        todate = toDate.value,
                        gat_name = if (gatName.value != null) {
                            gatName.value!!.gat
                        } else {
                            ""
                        },
                        village_id = if (villageName.value != null) {
                            villageName.value!!.id.toString()
                        } else {
                            ""
                        },
                        complaint_id = if (selectedComplaint.value!=null) {
                            selectedComplaint.value!!.id
                        }else{
                            ""
                        },
                        complaint_state = selectedComplaintStatus.value.lowercase()
                    )
                )
            },
            Purple80
        )
    }

}

@Composable
fun VillageCountListUI(
    list: List<VillageCount>,
    navigator: NavHostController?,
) {

    LazyColumn {
        items(count = list.size, key = { it }) {
            VillageItem(list[it], navigator)
        }
    }

}

@Composable
fun VillageItem(villageCount: VillageCount, navigator: NavHostController?) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                navigator?.navigate(
                    Screens.ComplaintList.route + "/${
                        villageCount.villageid.replace(
                            "/",
                            "::"
                        )
                    }"
                )
            }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = villageCount.villagename, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(5.dp))
            if (villageCount.solved_complaints.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.solved) + "- " + villageCount.solved_complaints,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

            if (villageCount.pending_complaints.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.pensing) + "- " + villageCount.pending_complaints,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
            if (villageCount.reject_complaints.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.rejected) + "- " + villageCount.reject_complaints,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

            if (villageCount.total_complaints.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.total) + "- " + villageCount.total_complaints,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }

    }


}


@Composable
private fun ShowPointDialog(
    fromDate: MutableState<String>,
    toDate: MutableState<String>,
    fromDateCompare: MutableState<String>,
    toDateCompare: MutableState<String>,
    villageName: MutableState<Village?>,
    gatName: MutableState<Village?>,
    selectedComplaint: MutableState<CategoryModel?>,
    selectedComplaintStatus: MutableState<String>,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    themeColor: Color,
) {
    val fromDialog = remember { mutableStateOf(false) }
    val toDialog = remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = MaterialTheme.colors.surface
        ) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState())
            ) {
                Text(
                    text = "Filter",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp),
                    color = themeColor
                )
                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Date",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                )

                OutlinedButton(
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(color = FontColor2, width = 1.dp),
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    onClick = {
                        fromDialog.value = true
                    }) {

                    Icon(Icons.Outlined.CalendarToday, contentDescription = "")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = fromDate.value.ifEmpty {
                        "From date"
                    }, textAlign = TextAlign.Start, style = MaterialTheme.typography.body1)
                }
                OutlinedButton(
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(color = FontColor2, width = 1.dp),
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    onClick = {
                        toDialog.value = true
                    }) {

                    Icon(Icons.Outlined.CalendarToday, contentDescription = "")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = toDate.value.ifEmpty {
                        "To date"
                    }, textAlign = TextAlign.Start, style = MaterialTheme.typography.body1)

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

                if (Cache.villages.isNotEmpty()) {


                    Text(
                        text = "गण",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                    )


                    val gatList = Cache.villages.distinctBy { it.gan }
                    DropDownSpinner(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .border(
                                shape = RoundedCornerShape(8.dp),
                                width = 1.dp, color =
                                FontColor2
                            ),
                        defaultText = "गण निवडा",
                        selectedItem = gatName.value,
                        onItemSelected = { _, item ->
                            gatName.value = item
                            villageName.value = null
                        },
                        itemList = gatList
                    )

                    Text(
                        text = "गाव",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                    )

                    val list = if (gatName.value != null) {
                        Cache.villages.filter { it.gan == gatName.value!!.gan }
                    } else {
                        Cache.villages
                    }
                    DropDownSpinner(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .border(
                                shape = RoundedCornerShape(8.dp),
                                width = 1.dp, color =
                                FontColor2
                            ),
                        defaultText = "गाव निवडा",
                        selectedItem = villageName.value,
                        onItemSelected = { _, item ->
                            villageName.value = item
                        },
                        itemList = list
                    )


                    Text(
                        text = "तक्रार श्रेणी",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                    )

                    val complaintList = Cache.complaintCategory
                    DropDownSpinner(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .border(
                                shape = RoundedCornerShape(8.dp),
                                width = 1.dp, color =
                                FontColor2
                            ),
                        defaultText = "तक्रार निवडा",
                        selectedItem = selectedComplaint.value,
                        onItemSelected = { _, item ->
                            selectedComplaint.value = item
                        },
                        itemList = complaintList
                    )


                    Text(
                        text = "तक्रार स्थिति",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                    )

                    val complainStates = arrayListOf("InProcess","Rejected","Complete")
                    DropDownSpinner(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .border(
                                shape = RoundedCornerShape(8.dp),
                                width = 1.dp, color =
                                FontColor2
                            ),
                        defaultText = "तक्रार निवडा",
                        selectedItem = selectedComplaintStatus.value,
                        onItemSelected = { _, item ->
                            selectedComplaintStatus.value = item
                        },
                        itemList = complainStates
                    )
                }


                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onClear) {
                        Text(
                            text = "Clear",
                            color = themeColor,
                            fontSize = 16.sp
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Apply",
                            color = themeColor,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview

fun VillageCountPre() {
    VillageItem(
        VillageCount(
            pending_complaints = "10",
            reject_complaints = "5",
            solved_complaints = "10",
            total_complaints = "100",
            villagename = "Nashik",
            villageid = "49"
        ),
        null
    )
}