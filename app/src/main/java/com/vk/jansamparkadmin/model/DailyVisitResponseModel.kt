package com.vk.jansamparkadmin.model

data class DailyVisitResponseModel(
    val data: List<DailyVisitDayModel>,
    val error: Any,
    val messages: String,
    val status: Int
)

data class PersonsVisited(
    val information: String,
    val name: String,
    val servey: String,
    val subject: String
)

data class DailyVisitDayModel(
    val date: String,
    val list: List<DailyVisitModel>
)

data class DailyVisitModel (
    val attachments: String?="",
    val birthdayinfo: String?="",
    val birthdayinfofile: String?="",
    val coordinator_id: Int,
    val createddate: String?="",
    val deathpersoninfo: String?="",
    val deathpersoninfofile: String?="",
    val deleted: Int,
    val deleted_at: String?="",
    val devinfo: String?="",
    val devinfofile: String?="",
    val drinkingwaterinfo: String?="",
    val drinkingwaterinfofile: String?="",
    val electricityinfo: String?="",
    val electricityinfofile: String?="",
    val govservantinfo: String?="",
    val govservantinfofile: String?="",
    val id: Int,
    val latitude: String?="",
    val longitude: String?="",
    val newschemes: String?="",
    val newschemesfile: String?="",
    val otherinfo: String?="",
    val otherinfofile: String?="",
    val persons_visited: List<PersonsVisited>,
    val politicalinfo: String?="",
    val politicalinfofile: String?="",
    val primarycarecenterinfo: String?="",
    val primarycarecenterinfofile: String?="",
    val rashanshopinfo: String?="",
    val rashanshopinfofile: String?="",
    val schoolinfo: String?="",
    val schoolinfofile: String?="",
    val updated_at: String?="",
    val veterinarymedicineinfo: String?="",
    val veterinarymedicineinfoinfo: String?="",
    val villageid: Int,
    val villagename: String?="",
    val watercanelinfo: String?="",
    val watercanelinfofile: String?="",
)