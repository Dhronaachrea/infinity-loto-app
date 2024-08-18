package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class EmailVerifyRequestData(

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("playerId")
    @Expose
    val playerId: Int? = PlayerInfo.getPlayerId(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("verificationCode")
    @Expose
    val verificationCode: String?,

    @SerializedName("verificationField")
    @Expose
    val verificationField: String = "email",

    @SerializedName("verificationType")
    @Expose
    val verificationType: String? = "PROFILE"
)