package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig

data class ForgotPasswordRequestData(

    @SerializedName("confirmPassword")
    @Expose
    val confirmPassword: String? = "",

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String?,

    @SerializedName("newPassword")
    @Expose
    val newPassword: String? = "",

    @SerializedName("otp")
    @Expose
    val otp: String? = ""
)