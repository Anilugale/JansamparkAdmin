package com.vk.jansamparkadmin.model

data class MarkAsUrgentReq(
    val coordinator_id: String,
    val isurgent: Int,
    val ticket_id: Int
)