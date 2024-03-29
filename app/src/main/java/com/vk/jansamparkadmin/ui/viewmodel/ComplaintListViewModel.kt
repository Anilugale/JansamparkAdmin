package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.WifiService
import com.vk.jansamparkadmin.model.ComplaintReq
import com.vk.jansamparkadmin.model.VillageFilterReq
import com.vk.jansamparkadmin.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintListViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Progress)
    val stateExpose = state.asStateFlow()


     fun getTotalCount(model: ComplaintReq) {
         if (WifiService.instance.isOnline()) {
             viewModelScope.launch {
                 state.value = Status.Progress
                 val login = service.getComplaintList(model)
                 viewModelScope.launch(Dispatchers.Main) {
                     if (login.isSuccessful) {
                         if (login.body() != null) {
                             if (login.body()!!.data.isNotEmpty()) {
                                 Cache.commentList.clear()
                                 Cache.commentList.addAll(login.body()!!.data)
                                 state.value = Status.SuccessComplaintList(login.body()!!.data)
                             } else {
                                 state.value = Status.Empty
                             }
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
         }else{
             state.value = Status.ErrorLogin(Cache.NO_INTERNET)
         }
    }


}