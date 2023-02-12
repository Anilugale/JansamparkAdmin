package com.vk.jansamparkadmin.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.TotalCount
import com.vk.jansamparkadmin.ui.theme.chipClose
import com.vk.jansamparkadmin.ui.theme.chipProgress
import com.vk.jansamparkadmin.ui.theme.chipReject
import com.vk.jansamparkadmin.ui.viewmodel.DashboardViewModel
import com.vk.jansamparkadmin.ui.viewmodel.Status


@Composable
fun Dashboard(navigator: NavHostController) {

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Dashboard") }) },
        bottomBar = { BottomNavigationBar(navController = navigator)},
        modifier = Modifier.background(color = MaterialTheme.colors.background)
    ) {


        val vModel: DashboardViewModel = hiltViewModel()
        val rememberVm = remember {
            vModel
        }

        when (val uiState = rememberVm.stateExpose.collectAsState().value) {
            Status.Empty -> {

            }
            is Status.ErrorLogin -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.msg)
                }
            }
            Status.Progress -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Status.SuccessDashboard -> {
                DashboardCountUI(uiState.model)
            }
            is Status.SuccessUser -> {

            }
            else -> {}
        }
    }


}

@Composable
fun DashboardCountUI(model: TotalCount) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {


            val arrayListOf = arrayListOf(
                ((model.pending_complaints.toFloat() / model.total_complaints.toFloat()) * 100),
                ((model.rejected_complaints.toFloat() / model.total_complaints.toFloat()) * 100),
                ((model.solved_complaints.toFloat() / model.total_complaints.toFloat()) * 100),
            )
            PieChart1(
                values = arrayListOf,
                colors = arrayListOf(chipProgress, chipReject, chipClose),
                legend = arrayListOf(
                    "${model.pending_complaints} / ${model.total_complaints} "+ stringResource(id = R.string.pensing),
                    "${model.rejected_complaints} / ${model.total_complaints} "+ stringResource(id = R.string.rejected),
                    "${model.solved_complaints} / ${model.total_complaints} "+ stringResource(id = R.string.solved)
                ),

            )
        }
    }
}




@Composable
fun PieChart1(
    values: List<Float> = listOf(15f, 35f, 50f),
    colors: List<Color> = listOf(Color(0xFF58BDFF), Color(0xFF125B7F), Color(0xFF092D40)),
    legend: List<String> = listOf("Mango", "Banana", "Apple"),
    size: Dp = 300.dp
) {

    // Sum of all the values
    val sumOfValues = values.sum()

    // Calculate each proportion value
    val proportions = values.map {
        it * 100 / sumOfValues
    }

    // Convert each proportions to angle
    val sweepAngles = proportions.map {
        360 * it / 100
    }

    Canvas(
        modifier = Modifier
            .size(size = size)
            .border(
                width = 2.dp,
                shape = CircleShape,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
    ) {

        var startAngle = -90f

        for (i in sweepAngles.indices) {
            drawArc(
                color = colors[i],
                startAngle = startAngle,
                sweepAngle = sweepAngles[i],
                useCenter = true
            )
            startAngle += sweepAngles[i]
        }

    }

    Spacer(modifier = Modifier.height(32.dp))

    Column {
        for (i in values.indices) {
            Spacer(modifier = Modifier.width(5.dp))
            DisplayLegend1(color = colors[i], legend = legend[i])
        }
    }

}

@Composable
fun DisplayLegend1(color: Color, legend: String) {
    Card(modifier = Modifier
        .padding(10.dp)
        .width(150.dp), backgroundColor = color) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = legend,
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }

}

@Composable
@Preview
fun DashPrv() {
    DashboardCountUI(
        TotalCount(
            pending_complaints = 15,
            rejected_complaints = 5,
            solved_complaints = 30,
            total_complaints = 50
        )
    )

}