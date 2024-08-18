package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmailCodeResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("verificationBy")
    @Expose
    val verificationBy: String?,

    var openDialog: Boolean = true,
)