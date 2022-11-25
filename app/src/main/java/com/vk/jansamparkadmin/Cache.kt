package com.vk.jansamparkadmin

import com.vk.jansamparkadmin.model.CategoryModel
import com.vk.jansamparkadmin.model.ComplaintModel
import com.vk.jansamparkadmin.model.Village

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
}