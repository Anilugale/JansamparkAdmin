package com.vk.jansamparkadmin.model

data class MessageListResponse(
    val data: List<MessageModel>,
    val error: Any,
    val messages: String?,
    val status: Int
)

data class MessageModel(
    val coordinator_id: Int,
    val createddate: String,
    val id: Int,
    val message: String,
    val name: String,
    val admin_name: String = "Admin",
)