package com.vk.jansamparkadmin.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.VillageCount
import com.vk.jansamparkadmin.model.VillageFilterReq
import com.vk.jansamparkadmin.ui.viewmodel.Status
import com.vk.jansamparkadmin.ui.viewmodel.VillageListViewModel


@Composable
fun VillageList(navigator: NavHostController?) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Dashboard") }) },
        bottomBar = { BottomNavigationBar(navController = navigator!!) }
    ) {
        val vModel: VillageListViewModel = hiltViewModel()
        val rememberVm = remember {
            vModel
        }
        val uiState = rememberVm.stateExpose.collectAsState().value
        Column {
            Row {

                val fromDialog = remember { mutableStateOf(false) }
                val fromDate = remember { mutableStateOf("") }
                val toDate = remember { mutableStateOf("") }
                val toDialog = remember { mutableStateOf(false) }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 10.dp),
                    onClick = {
                        fromDialog.value = true
                    }) {

                    Icon(Icons.Outlined.CalendarToday, contentDescription = "")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = fromDate.value.ifEmpty {
                        "From date"
                    }, style = MaterialTheme.typography.body1)
                }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 10.dp),
                    onClick = {
                        toDialog.value = true
                    }) {

                    Icon(Icons.Outlined.CalendarToday, contentDescription = "")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = toDate.value.ifEmpty {
                        "To date"
                    }, style = MaterialTheme.typography.body1)


                }
                if (fromDate.value.isNotEmpty() && toDate.value.isNotEmpty()) {
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(horizontal = 2.dp, vertical = 10.dp),
                        onClick = {
                            vModel.getTotalCount(VillageFilterReq(fromDate.value, toDate.value))
                        }) {
                        Icon(Icons.Outlined.Search, contentDescription = "")
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Search", style = MaterialTheme.typography.body1)
                    }
                }



                if (fromDialog.value) {
                    rememberDatePicker(
                        {
                            fromDate.value = it
                            fromDialog.value = false
                        },
                    ) {
                        fromDialog.value = false
                    }
                }

                if (toDialog.value) {
                    rememberDatePicker(
                        {
                            toDate.value = it
                            toDialog.value = false
                        },
                    ) {
                        toDialog.value = false
                    }
                }
            }






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

}

@Composable
fun VillageCountListUI(
    list: List<VillageCount>,
    navigator: NavHostController?,
) {

    LazyColumn {
        items(count = list.size, key = { it }) {
            VillageItem(list[it],navigator)
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
                        villageCount.villagename.replace(
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
@Preview

fun VillageCountPre() {
    VillageItem(
        VillageCount(
            pending_complaints = "10",
            reject_complaints = "5",
            solved_complaints = "10",
            total_complaints = "100",
            villagename = "Nashik"
        ),
        null
    )
}