package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.Cache
import com.vk.jansamparkadmin.model.*
import com.vk.jansamparkadmin.service.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val service: Service) : ViewModel() {

    private var state: MutableStateFlow<MsgListStatus> = MutableStateFlow(MsgListStatus.Progress)
    val stateExpose = state.asStateFlow()


    fun getVillageList() {
        viewModelScope.launch{
            val msgList = service.getMsgList()
            if(msgList.isSuccessful && msgList.body()!=null){
                if( msgList.body()!!.status == 200){
                    Cache.msgList = msgList.body()!!.data as ArrayList<MessageModel>
                    state.value = MsgListStatus.SuccessMsgList(Cache.msgList)
                }else{
                    state.value = MsgListStatus.Error(msgList.body()!!.messages?:"server Error")
                }
            }else{
                state.value = MsgListStatus.Error("server Error")
            }

        }
    }


}

sealed class MsgListStatus{
    object Progress:MsgListStatus()
    object Empty:MsgListStatus()
    class SuccessMsgList(val msgList: List<MessageModel>):MsgListStatus()
    class Error(val msg: String?):MsgListStatus()
}
