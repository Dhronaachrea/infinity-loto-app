package com.skilrock.infinitylotoapp.data_class.request_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class FundTransferFromWalletRequestData(

    @SerializedName("amount")
    @Expose
    val amount: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String? = BuildConfig.DOMAIN_NAME,

    @SerializedName("ipAddress")
    @Expose
    val ipAddress: String?,

    @SerializedName("machineId")
    @Expose
    val machineId: String?,

    @SerializedName("merchantId")
    @Expose
    val merchantId: String?,

    @SerializedName("playerId")
    @Expose
    val playerId: String? = PlayerInfo.getPlayerId().toString(),

    @SerializedName("playerToken")
    @Expose
    val playerToken: String? = PlayerInfo.getPlayerToken(),

    @SerializedName("refTxnNo")
    @Expose
    val refTxnNo: String?,

    @SerializedName("txnType")
    @Expose
    val txnType: String? = "transfer_Out"
)