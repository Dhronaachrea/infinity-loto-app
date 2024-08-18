package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class MyTransactionRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("fromDate")
    @Expose
    val fromDate: String?,

    @SerializedName("limit")
    @Expose
    val limit: String? = "200",

    @SerializedName("offset")
    @Expose
    val offset: String? = "0",

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("toDate")
    @Expose
    val toDate: String?,

    @SerializedName("txnType")
    @Expose
    val txnType: String?
)