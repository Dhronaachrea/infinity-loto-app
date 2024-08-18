package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequestData(

    @SerializedName("deviceType")
    @Expose
    val deviceType: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String?,

    @SerializedName("loginDevice")
    @Expose
    val loginDevice: String?,

    @SerializedName("loginToken")
    @Expose
    val loginToken: String?,

    @SerializedName("password")
    @Expose
    val password: String?,

    @SerializedName("requestIp")
    @Expose
    val requestIp: String?,

    @SerializedName("trackingCipher")
    @Expose
    val trackingCipher: String?,

    @SerializedName("userAgent")
    @Expose
    val userAgent: String?,

    @SerializedName("userName")
    @Expose
    val userName: String?,

    @SerializedName("fcmIdAndroid")
    @Expose
    val fcmIdAndroid: String?
)