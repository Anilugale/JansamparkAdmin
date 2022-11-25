package com.vk.jansamparkadmin.model

data class VillageFilterReq(
    val fromdate: String,
    val todate: String,
    val gat_name:String,
    val village_id:String,
    val complaint_id:String,
    val complaint_state:String
)