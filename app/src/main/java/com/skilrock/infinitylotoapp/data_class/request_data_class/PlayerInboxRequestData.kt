package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class PlayerInboxRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("limit")
    @Expose
    val limit: Int? = 800,

    @SerializedName("offset")
    @Expose
    val offset: Int? = 0,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken()
)