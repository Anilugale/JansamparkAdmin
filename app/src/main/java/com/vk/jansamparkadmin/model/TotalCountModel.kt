package com.vk.jansamparkadmin.model

data class TotalCountModel(
    val data: List<TotalCount>,
    val error: Boolean,
    val messages: String,
    val status: Int
)

data class TotalCount(
    val pending_complaints: Int,
    val rejected_complaints: Int,
    val solved_complaints: Int,
    val total_complaints: Int
)