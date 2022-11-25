package com.vk.jansamparkadmin.ui.screen

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vk.jansamparkadmin.R
import com.vk.jansamparkadmin.model.CategoryModel
import com.vk.jansamparkadmin.model.Village
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
private val ELEMENT_HEIGHT = 48.dp


@Composable
fun DropDownSpinner(
    modifier: Modifier = Modifier,
    defaultText: String = "Select...",
    selectedItem: Village?,
    onItemSelected: (Int, Village) -> Unit,
    itemList: List<Village>,
) {
    var isOpen by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .height(ELEMENT_HEIGHT),
        contentAlignment = Alignment.CenterStart
    ) {
        if (selectedItem == null || selectedItem.toString().isEmpty()) {
            Text(
                text = defaultText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 3.dp),
                color = MaterialTheme.colors.onSurface.copy(.45f)
            )
        }

        Text(
            text = selectedItem?.village?:"",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, bottom = 3.dp),
            color = MaterialTheme.colors.onSurface
        )


        DropdownMenu(
            modifier = Modifier.fillMaxWidth(.85f),
            expanded = isOpen,
            onDismissRequest = {
                isOpen = false
            },
        ) {
            itemList?.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        isOpen = false
                        onItemSelected(index, item)
                    }
                ) {
                    Text(item.village)
                }
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(24.dp),

            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Dropdown"
        )

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(
                    onClick = { isOpen = true }
                )
        )
    }
}


@Composable
fun DropDownSpinner(
    modifier: Modifier = Modifier,
    defaultText: String = "Select...",
    selectedItem: CategoryModel?,
    onItemSelected: (Int, CategoryModel) -> Unit,
    itemList: List<CategoryModel>,
) {
    var isOpen by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .height(ELEMENT_HEIGHT),
        contentAlignment = Alignment.CenterStart
    ) {
        if (selectedItem == null || selectedItem.toString().isEmpty()) {
            Text(
                text = defaultText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 5.dp, bottom = 3.dp),
                color = MaterialTheme.colors.onSurface.copy(.45f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }

        Text(
            text = selectedItem?.categorie?:"",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, bottom = 3.dp),
            color = MaterialTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start
        )


        DropdownMenu(
            modifier = Modifier.fillMaxWidth(.85f),
            expanded = isOpen,
            onDismissRequest = {
                isOpen = false
            },
        ) {
            itemList?.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        isOpen = false
                        onItemSelected(index, item)
                    }
                ) {
                    Text(item.categorie)
                }
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(24.dp),

            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Dropdown"
        )

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(
                    onClick = { isOpen = true }
                )
        )
    }
}


@Composable
fun DropDownSpinner(
    modifier: Modifier = Modifier,
    defaultText: String = "Select...",
    selectedItem: String,
    onItemSelected: (Int, String) -> Unit,
    itemList: List<String>?,
) {
    var isOpen by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .height(ELEMENT_HEIGHT),
        contentAlignment = Alignment.CenterStart
    ) {
        if (selectedItem == null || selectedItem.isEmpty()) {
            Text(
                text = defaultText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 3.dp),
                color = MaterialTheme.colors.onSurface.copy(.45f)
            )
        }

        Text(
            text = selectedItem,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, bottom = 3.dp),
            color = MaterialTheme.colors.onSurface
        )


        DropdownMenu(
            modifier = Modifier.fillMaxWidth(.85f),
            expanded = isOpen,
            onDismissRequest = {
                isOpen = false
            },
        ) {
            itemList?.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        isOpen = false
                        onItemSelected(index, item)
                    }
                ) {
                    Text(item)
                }
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(24.dp),

            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Dropdown"
        )

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(
                    onClick = { isOpen = true }
                )
        )
    }
}

