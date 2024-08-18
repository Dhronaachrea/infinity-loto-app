package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.DEVICE_TYPE
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class DepositUiRequestData(

    @SerializedName("currencyCode")
    @Expose
    val currencyCode: String? = PlayerInfo.getCurrency(),

    @SerializedName("deviceType")
    @Expose
    val deviceType: String? = DEVICE_TYPE,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("txnType")
    @Expose
    val txnType: String? = "DEPOSIT",

    @SerializedName("userAgent")
    @Expose
    val userAgent: String? = System.getProperty("http.agent")
)