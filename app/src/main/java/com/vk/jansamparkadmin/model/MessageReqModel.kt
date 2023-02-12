package com.vk.jansamparkadmin.model

data class MessageReqModel(
    val coordinator_id: String,
    val device_token: String,
    val message: String,
    val from: String,
)


data class MessageListReqModel(
    val admin_name: String
)