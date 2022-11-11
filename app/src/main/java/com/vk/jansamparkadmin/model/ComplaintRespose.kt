package com.vk.jansamparkadmin.model

data class ComplaintResponse(
    val data: List<ComplaintModel>,
    val error: Boolean,
    val messages: String,
    val status: Int
)


data class ComplaintModel(
    val attachments: String?,
    val categorie: String,
    val categorie_id: String,
    val comments: List<String>,
    val coordinator_id: Int,
    val id: Int,
    var isurgent: Int,
    val mobileno: String,
    val ticket_date: String,
    val ticket_exp: String,
    val ticket_status: String,
    val villagename: String
)