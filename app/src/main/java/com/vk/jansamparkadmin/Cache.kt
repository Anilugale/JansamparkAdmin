package com.vk.jansamparkadmin

import com.vk.jansamparkadmin.model.*

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
    var villages: List<Village> = arrayListOf()
    var complaintCategory: ArrayList<CategoryModel> = arrayListOf()
    var coordinateList: List<CoordinateModel> = arrayListOf()
    var msgList: ArrayList<MessageModel> = arrayListOf()


    const val NO_INTERNET: String = "No Internet Connection"
}