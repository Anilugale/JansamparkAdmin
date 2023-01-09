package com.vk.jansamparkadmin.model



data class CoOrdinateResposnse(
    val data: List<CoordinateModel>,
    val error: String?,
    val messages: String,
    val status: Int
)
data class CoordinateModel(
    val age: String,
    val category: Any,
    val createdAt: String,
    val deviceId: String,
    val dob: String,
    val emailid: String,
    val gatid: Int,
    val id: Int,
    val mobileno: String,
    val name: String,
    val otp: Int,
    val password: String,
    val photo: Any,
    val updatedAt: String,
    val village: Any
)