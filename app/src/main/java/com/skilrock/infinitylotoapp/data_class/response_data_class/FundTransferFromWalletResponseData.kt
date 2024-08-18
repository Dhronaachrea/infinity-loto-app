package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FundTransferFromWalletResponseData(

    @SerializedName("amount")
    @Expose
    val amount: Double?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("playerId")
    @Expose
    val playerId: Int?,

    @SerializedName("playerName")
    @Expose
    val playerName: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("errorMsg")
    @Expose
    val errorMsg: String?,

    @SerializedName("totalBal")
    @Expose
    val totalBal: Double?,

    @SerializedName("txnId")
    @Expose
    val txnId: Int?
)