package com.vk.jansamparkadmin.model

data class VillageCountResponse(
    val data: List<VillageCount>,
    val error: Boolean,
    val messages: String,
    val status: Int
)

data class VillageCount(
    val pending_complaints: String,
    val reject_complaints: String,
    val solved_complaints: String,
    val total_complaints: String,
    val villagename: String,
    val villageid: String
)