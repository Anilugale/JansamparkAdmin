package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.model.Comment
import com.vk.jansamparkadmin.model.ComplaintModel
import com.vk.jansamparkadmin.model.MarkAsUrgentReq
import com.vk.jansamparkadmin.service.Service

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ComplaintDetailsVM @Inject constructor(val service: Service) : ViewModel() {
    private var state: MutableStateFlow<DetailsState> = MutableStateFlow(DetailsState.Empty)
    val stateExpose = state.asStateFlow()


    private var stateMark: MutableStateFlow<MarkImportant> = MutableStateFlow(MarkImportant.Empty)
    val stateExposeMark = stateMark.asStateFlow()

    fun getList(isProgress: Boolean = false, comment: ComplaintModel?) {
        var comments = comment?.comments
        if (comments == null) {
            comments = arrayListOf()
        }
        if (isProgress) {
            viewModelScope.launch {
                state.value = DetailsState.Success(comments)
            }
        } else {
            state.value = DetailsState.Success(comments)
        }
    }

    fun markThisImportant(comment: ComplaintModel) {
        viewModelScope.launch {
            val markAsUrgent = service.markAsUrgent(
                MarkAsUrgentReq(
                    coordinator_id = comment.coordinator_id.toString(),
                    isurgent = 1,
                    ticket_id = comment.id
                )
            )

            if(markAsUrgent.isSuccessful){
                if (markAsUrgent.code() == 200) {
                    comment.isurgent = 1
                    stateMark.value = MarkImportant.Success(markAsUrgent.body()!!.messages)
                }else{
                    stateMark.value = MarkImportant.Failed(markAsUrgent.body()!!.messages)
                }
            }else{
                stateMark.value = MarkImportant.Failed(markAsUrgent.message())
            }
        }

    }
}


sealed class MarkImportant {
    class Success(val msg:String) : MarkImportant()
    class Failed(val msg: String) : MarkImportant()
    object Empty : MarkImportant()

}

sealed class DetailsState {
    class Success(val commentList: List<String>) : DetailsState()
    class SuccessDelete(val msg: String, val commentList: ArrayList<Comment>) : DetailsState()
    class FailedDelete(val msg: String) : DetailsState()
    object Process : DetailsState()
    object Empty : DetailsState()
}