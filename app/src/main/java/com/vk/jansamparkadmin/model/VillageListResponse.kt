package com.vk.jansamparkadmin.model


data class VillageListResponse(
    val data: VillageDetails,
    val error: Any,
    val messages: String,
    val status: Int
)

data class Village(
    val coordinatorid: Int,
    val gan: String,
    val gat: String,
    val id: Int,
    val infourl: String,
    val taluka: String,
    val village: String
)

data class VillageDetails(
    val taluka: List<String>,
    val villages: List<Village>
)