package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class AddNewAccountRequestData(

    @SerializedName("accHolderName")
    @Expose
    val accHolderName: String?,

    @SerializedName("accNum")
    @Expose
    val accNum: String?,

    @SerializedName("accType")
    @Expose
    val accType: String? = "SAVING",

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("isNewPaymentAcc")
    @Expose
    val isNewPaymentAcc: String? = "Y",

    @SerializedName("paymentTypeCode")
    @Expose
    val paymentTypeCode: String?,

    @SerializedName("paymentTypeId")
    @Expose
    val paymentTypeId: String?,

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("subTypeId")
    @Expose
    val subTypeId: String?,

    @SerializedName("currencyCode")
    @Expose
    val currencyCode: String? = "",

    @SerializedName("verifyOtp")
    @Expose
    val verifyOtp: String? = null
)