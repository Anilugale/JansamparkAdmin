package com.vk.jansamparkadmin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface


import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.vk.jansamparkadmin.ui.screen.Screens
import com.vk.jansamparkadmin.ui.screen.ShowNavGraph
import com.vk.jansamparkadmin.ui.theme.JansamparkAdminTheme
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JansamparkAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material.MaterialTheme.colors.background
                ) {
                    val sharedPreferences = LocalContext.current.getSharedPreferences(
                        stringResource(id = R.string.app_name),
                        Context.MODE_PRIVATE
                    )
                    if (sharedPreferences.getBoolean("isLogin",false) && sharedPreferences.getString("user",null) !=null) {
                        ShowNavGraph(Screens.DailyVisit.route)
                    }else {
                        ShowNavGraph(Screens.Login.route)
                    }
                }
            }
        }
    }
}


