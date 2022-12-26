package com.vk.jansamparkadmin.model

data class ComplaintReq(
    val fromdate: String,
    val todate: String,
    val village_id: String,
    val villagelist :Boolean =true
)