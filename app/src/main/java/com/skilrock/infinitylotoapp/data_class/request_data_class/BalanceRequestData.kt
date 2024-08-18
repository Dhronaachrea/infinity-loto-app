package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig

data class BalanceRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("playerId")
    @Expose
    val playerId: String?,

    @SerializedName("playerToken")
    @Expose
    val playerToken: String?,

    @SerializedName("refill")
    @Expose
    val refill: Boolean? = false,

    @SerializedName("ussd")
    @Expose
    val ussd: Boolean? = false,

    @SerializedName("walletType")
    @Expose
    val walletType: String? = "CASH"
)