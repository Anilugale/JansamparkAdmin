package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.model.*
import com.vk.jansamparkadmin.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val service: Service) : ViewModel() {

    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()
    var isProcess = false
    fun login(loginReq: LoginReqModel) {
        viewModelScope.launch (Dispatchers.IO) {
            state.value = Status.Progress
            isProcess = true
            viewModelScope.launch(Dispatchers.Main) {
                val login = service.userLogin(loginReq)
                if (login.isSuccessful) {
                    if (login.body() != null) {
                        state.value = Status.SuccessUser(login.body()!!.data)
                    } else {
                        if (login.body() != null) {
                            state.value = Status.ErrorLogin(login.body()!!.messages)
                        } else {
                            state.value = Status.ErrorLogin(login.errorBody().toString())
                        }
                    }
                }
                isProcess = false
            }
        }
    }


}

sealed class Status{
    object Progress:Status()
    object Empty:Status()
    class ErrorLogin(val msg:String):Status()
    class SuccessUser(val user: List<Admin>):Status()
    class SuccessDashboard(val model: TotalCount):Status()
    class SuccessVillage(val list: List<VillageCount>):Status()
    class SuccessComplaintList(val list: List<ComplaintModel>):Status()
}