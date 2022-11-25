package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.model.Admin
import com.vk.jansamparkadmin.model.LoginReqModel
import com.vk.jansamparkadmin.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val service: Service) : ViewModel() {

    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Progress)
    val stateExpose = state.asStateFlow()

    init {
        getTotalCount()
        getVillageList()
        getComplaintCategory()
    }

    private fun getVillageList() {
        viewModelScope.launch{
            val villageList = service.getVillageList()
            if (villageList.isSuccessful) {
                if (villageList.body()!=null) {
                    Cache.villages = villageList.body()!!.data.villages
                }
            }

        }
    }

    private fun getComplaintCategory() {
        viewModelScope.launch{
            val villageList = service.getComplaintCategory()
            if (villageList.isSuccessful) {
                if (villageList.body()!=null) {
                    Cache.complaintCategory = villageList.body()!!.data
                }
            }

        }
    }

    private fun getTotalCount() {
        viewModelScope.launch{
            state.value = Status.Progress
            viewModelScope.launch(Dispatchers.Main) {
                val login = service.getTotalCount()
                if (login.isSuccessful) {
                    if (login.body() != null) {
                        state.value = Status.SuccessDashboard(login.body()!!.data[0])
                    } else {
                        if (login.body() != null) {
                            state.value = Status.ErrorLogin(login.body()!!.messages)
                        } else {
                            state.value = Status.ErrorLogin(login.errorBody().toString())
                        }
                    }
                }
            }
        }
    }

}
