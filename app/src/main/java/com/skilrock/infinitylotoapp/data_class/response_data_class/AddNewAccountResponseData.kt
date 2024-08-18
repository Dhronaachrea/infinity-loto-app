package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddNewAccountResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("errorMsg")
    @Expose
    val errorMsg: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?
)