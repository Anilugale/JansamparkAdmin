package com.vk.jansamparkadmin.model

data class LoginResposeModel(
    val count: Int,
    val data: List<Admin>,
    val error: Any,
    val messages: String,
    val status: Int
)


data class Admin(
    val email: String,
    val id: Int,
    val name: String,
    val password: String
)

data class LoginReqModel(
    val email: String,
    val password: String
)