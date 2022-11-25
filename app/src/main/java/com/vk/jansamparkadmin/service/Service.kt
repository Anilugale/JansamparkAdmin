package com.vk.jansamparkadmin.service

import com.vk.jansamparkadmin.model.*
import com.vk.jansamparkadmin.util.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface Service {

    @POST(Constants.loginUrl)
    suspend fun userLogin(@Body req:LoginReqModel): Response<LoginResposeModel>

    @POST(Constants.gettotalcount)
    suspend fun getTotalCount(): Response<TotalCountModel>

    @POST(Constants.gettotalcountbyvillage)
    suspend fun getTotalCountVillage(@Body model: VillageFilterReq): Response<VillageCountResponse>

    @POST(Constants.gettotalcountbyvillage)
    suspend fun getComplaintList(@Body model: ComplaintReq): Response<ComplaintResponse>



    @POST(Constants.markcomplainturgent)
    suspend fun markAsUrgent(@Body model:MarkAsUrgentReq): Response<MarkAsUrgentResponse>

    @POST(Constants.getvillages)
    suspend fun getVillageList(): Response<VillageListResponse>

    @POST("api/getcategories")
    suspend fun getComplaintCategory(): Response<ComplaintCategory>
}