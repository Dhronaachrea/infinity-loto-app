package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.getCurrentDateYMD
import com.skilrock.infinitylotoapp.utility.getPreviousDateYMD

data class DepositPendingRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("fromDate")
    @Expose
    val fromDate: String? = getPreviousDateYMD(180),

    @SerializedName("toDate")
    @Expose
    val toDate: String? = getCurrentDateYMD(),

    @SerializedName("limit")
    @Expose
    val limit: String? = "100",

    @SerializedName("offset")
    @Expose
    val offset: String? = "1",

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("txnType")
    @Expose
    val txnType: String?

)