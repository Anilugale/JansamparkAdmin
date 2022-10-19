package com.vk.jansamparkadmin

import com.vk.jansamparkadmin.model.ComplaintModel

object Cache {
    fun getComplaintFromID(id: Int):ComplaintModel? {
        return try {
                  commentList.single { it.id == id }
        }catch(e :Exception) {
            null
        }
    }

    fun clear() {
         commentList.clear()
    }

    val commentList = arrayListOf<ComplaintModel>()

}