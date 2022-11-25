package com.vk.jansamparkadmin.model


data class ComplaintCategory(
    val data: ArrayList<CategoryModel>,
    val error: Boolean,
    val messages: String,
    val status: Int
)


data class CategoryModel(
    val categorie: String,
    val id: String
)