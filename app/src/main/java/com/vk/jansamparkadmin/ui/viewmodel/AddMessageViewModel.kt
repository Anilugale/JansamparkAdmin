package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.model.*
import com.vk.jansamparkadmin.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMessageViewModel @Inject constructor(private val service: Service) : ViewModel() {

    private var state: MutableStateFlow<AddMessageState> = MutableStateFlow(AddMessageState.Progress)
    val stateExpose = state.asStateFlow()


    private var state1: MutableStateFlow<AddMessageState> = MutableStateFlow(AddMessageState.Empty)
    val stateExpose1 = state1.asStateFlow()

    init {
        getTotalCount()
    }



    private fun getTotalCount() {
        viewModelScope.launch{
            state.value = AddMessageState.Progress
            if (Cache.coordinateList.isEmpty()) {
                viewModelScope.launch(Dispatchers.Main) {
                    val coordinateList = service.getCoordinateList()
                    if (coordinateList.isSuccessful && coordinateList.body()!=null &&  coordinateList.body()!!.data!=null) {
                        Cache.coordinateList =  coordinateList.body()!!.data
                        state.value = AddMessageState.SuccessCoList(coordinateList.body()!!.data)
                    }else{
                        state.value = AddMessageState.ERROR(coordinateList.body()!!.error)
                    }
                }
            }else{
                state.value = AddMessageState.SuccessCoList(Cache.coordinateList)
            }
        }
    }

    fun sendMessage(value:MessageReqModel) {
        viewModelScope.launch{
            state1.value = AddMessageState.Progress

            viewModelScope.launch(Dispatchers.Main) {
                val response = service.sendMsg(value)
                if(response.isSuccessful && response.body()!=null && response.body()!!.status == 200){
                    state1.value = AddMessageState.SuccessMSG(response.body()!!.messages!!)
                }else{
                    state1.value = AddMessageState.ERROR(response.body()!!.messages?:"Server Error")
                }
            }
        }
    }

}


sealed class AddMessageState{
    object Progress:AddMessageState()
    object Empty:AddMessageState()
    class SuccessCoList(val list: List<CoordinateModel>?):AddMessageState()
    class SuccessMSG(val msg: String):AddMessageState()
    class ERROR(val msg: String?):AddMessageState()
}
