package com.vk.jansamparkadmin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.installations.Utils
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.WifiService
import com.vk.jansamparkadmin.model.CoordinateModel
import com.vk.jansamparkadmin.model.DailyVisitDayModel
import com.vk.jansamparkadmin.model.DailyVisitReq
import com.vk.jansamparkadmin.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailVisitVM @Inject constructor(val service:Service):ViewModel() {
    private var state: MutableStateFlow<DailyVisitState> = MutableStateFlow(DailyVisitState.Progress)
    val stateExpose = state.asStateFlow()


    private var state1: MutableStateFlow<DailyVisitState> = MutableStateFlow(DailyVisitState.Empty)
    val stateExpose1 = state1.asStateFlow()

    init {
        getTotalCount()
    }

     fun getDailyVisits(model :DailyVisitReq) {
        viewModelScope.launch{
            if(WifiService.instance.isOnline()) {
                val data = service.getDailyVisitOfCoordinator(model = model)
                if (data.isSuccessful && data.body() != null) {
                    Log.d("$$", "getDailyVisits: " + data.body()!!.data)
                    state1.value = DailyVisitState.SuccessDailyList(data.body()!!.data)
                } else {
                    state1.value = DailyVisitState.ERROR(data.message())
                }
            }else{
                state.value = DailyVisitState.ERROR(Cache.NO_INTERNET)
            }
        }
    }

    private fun getTotalCount() {
        viewModelScope.launch{
            state.value = DailyVisitState.Progress
            if(WifiService.instance.isOnline()) {
                if (Cache.coordinateList.isEmpty()) {
                    viewModelScope.launch(Dispatchers.Main) {
                        val coordinateList = service.getCoordinateList()
                        if (coordinateList.isSuccessful && coordinateList.body() != null && coordinateList.body()!!.data != null) {
                            Cache.coordinateList = coordinateList.body()!!.data
                            state.value =
                                DailyVisitState.SuccessCoList(coordinateList.body()!!.data)
                        } else {
                            state.value = DailyVisitState.ERROR(coordinateList.body()!!.error)
                        }
                    }
                } else {
                    state.value = DailyVisitState.SuccessCoList(Cache.coordinateList)
                }
            }else{
                state.value = DailyVisitState.ERROR(Cache.NO_INTERNET)
            }
        }
    }
}

sealed class DailyVisitState{
    object Progress:DailyVisitState()
    object Empty:DailyVisitState()
    class SuccessCoList(val list: List<CoordinateModel>?):DailyVisitState()
    class SuccessDailyList(val list: List<DailyVisitDayModel>):DailyVisitState()
    class SuccessMSG(val msg: String):DailyVisitState()
    class ERROR(val msg: String?):DailyVisitState()
}
