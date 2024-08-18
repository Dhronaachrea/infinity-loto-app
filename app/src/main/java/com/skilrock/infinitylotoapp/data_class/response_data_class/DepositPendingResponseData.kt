package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DepositPendingResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String?,

    @SerializedName("txnList")
    @Expose
    val txnList: ArrayList<Txn?>?
) {
    data class Txn(

        @SerializedName("amount")
        @Expose
        val amount: Double?,

        @SerializedName("status")
        @Expose
        val status: String?,

        @SerializedName("txnDate")
        @Expose
        val txnDate: String?,

        @SerializedName("userTxnId")
        @Expose
        val userTxnId: Int?
    )
}