package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlayerInboxReadResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("unreadMsgCount")
    @Expose
    val unreadMsgCount: Int?,

    @SerializedName("inboxId")
    @Expose
    var inboxId: Int?,

    @SerializedName("position")
    @Expose
    var position: Int?
)