package com.vk.jansamparkadmin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.jansamparkadmin.model.Comment
import com.vk.jansamparkadmin.model.ComplaintModel
import com.vk.jansamparkadmin.service.Service

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ComplaintDetailsVM @Inject constructor(val service: Service) : ViewModel() {
    private var state: MutableStateFlow<DetailsState> = MutableStateFlow(DetailsState.Empty)
    val stateExpose = state.asStateFlow()

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

}

sealed class DetailsState {
    class Success(val commentList: ArrayList<Comment>) : DetailsState()
    class SuccessDelete(val msg: String, val commentList: ArrayList<Comment>) : DetailsState()
    class FailedDelete(val msg: String) : DetailsState()
    object Process : DetailsState()
    object Empty : DetailsState()
}