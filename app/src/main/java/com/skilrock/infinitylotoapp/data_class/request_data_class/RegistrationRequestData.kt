package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.DEVICE_TYPE
import com.skilrock.infinitylotoapp.utility.LOGIN_DEVICE
import com.skilrock.infinitylotoapp.utility.REQUEST_IP

data class RegistrationRequestData(

    @SerializedName("countryCode")
    @Expose
    val countryCode: String? = BuildConfig.COUNTRY_CODE,

    @SerializedName("currencyId")
    @Expose
    val currencyId: Int? = BuildConfig.CURRENCY_ID,

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = DEVICE_TYPE,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("firstName")
    @Expose
    val firstName: String?,

    @SerializedName("lastName")
    @Expose
    val lastName: String?,

    @SerializedName("loginDevice")
    @Expose
    val loginDevice: String? = LOGIN_DEVICE,

    @SerializedName("mobileNo")
    @Expose
    val mobileNo: String?,

    @SerializedName("password")
    @Expose
    val password: String?,

    @SerializedName("registrationType")
    @Expose
    val registrationType: String? = "FULL",

    @SerializedName("requestIp")
    @Expose
    val requestIp: String? = REQUEST_IP,

    @SerializedName("referCode")
    @Expose
    val referCode: String?,

    @SerializedName("userAgent")
    @Expose
    val userAgent: String? = System.getProperty("http.agent"),

    @SerializedName("userName")
    @Expose
    val userName: String?,

    @SerializedName("otp")
    @Expose
    val otp: String?
)