package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JackpotAndTimerRequestData(

    @SerializedName("deviceType")
    @Expose
    val deviceType: String = "MOBILE"
)