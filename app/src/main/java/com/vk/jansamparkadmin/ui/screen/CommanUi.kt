package com.vk.jansamparkadmin.ui.screen

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.ui.theme.Purple80
import java.util.*


@Composable
fun BottomNavigationBar(navController: NavHostController) {

    BottomNavigation(

        // set background color
        backgroundColor = Purple80
    ) {

        // observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // observe current route to change the icon
        // color,label color when navigated
        val currentRoute = navBackStackEntry?.destination?.route

        // Bottom nav items we declared
        // Place the bottom nav items
        BottomNavigationItem(

            // it currentRoute is equal then its selected route
            selected = currentRoute == Screens.Dashboard.route,

            // navigate on click
            onClick = {
                navController.navigate(Screens.Dashboard.route) {
                    popUpTo(0)
                }
            },

            // Icon of navItem
            icon = {

                Icon(
                    imageVector = Screens.Dashboard.icon!!,
                    contentDescription = stringResource(id = R.string.dashboard)
                )
            },

            // label
            label = {
                Text(
                    text = stringResource(id = R.string.dashboard),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            alwaysShowLabel = false
        )

        BottomNavigationItem(

            // it currentRoute is equal then its selected route
            selected = currentRoute == Screens.VillageList.route,

            // navigate on click
            onClick = {
                navController.navigate(Screens.VillageList.route) {
                    popUpTo(0)
                }
            },

            // Icon of navItem
            icon = {
                Icon(
                    imageVector = Screens.VillageList.icon!!,
                    contentDescription = stringResource(id = R.string.village_list)
                )
            },

            // label
            label = {
                Text(
                    text = stringResource(id = R.string.village_list),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            alwaysShowLabel = false
        )


    }


}

@Composable
fun rememberDatePicker(OnClose: (date: String,time:String) -> Unit, OnCancel: () -> Unit) {
    val context = LocalContext.current
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerDialogTheme,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            println("$year, $month, $dayOfMonth")
            OnClose("$year-$month-$dayOfMonth","$year$month$dayOfMonth")
        },
        mYear, mMonth, mDay
    )
    datePickerDialog.setOnCancelListener {
        OnCancel()
    }
    datePickerDialog.show()
}


@Composable
fun ShowProgressDialog(function: () -> Unit) {
    Dialog(
        onDismissRequest = { function() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(backgroundColor = Color.White) {
            Box(contentAlignment = Alignment.Center) {}
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(20.dp)
            )
        }
    }
}



@Composable
fun ShowAlertDialog(value: String, function: () -> Unit) {
    val color = if (isSystemInDarkTheme()) {
        Color.White
    }else{
        Color.Black
    }
    AlertDialog(
        onDismissRequest = function,
        confirmButton = {
            TextButton(onClick = {
                function()
            })
            {
                Text(
                    text = "Okay",
                    color = color,
                    fontSize = 14.sp,
                )
            }
        },
        text = { Text(text = value, color = color, fontSize = 14.sp) },
    )
}

 fun Context.toast(msg:String){
     Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}
