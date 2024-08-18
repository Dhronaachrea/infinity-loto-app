package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateProfileResponseData(

    @SerializedName("docUploadStatus")
    @Expose
    val docUploadStatus: String?,

    @SerializedName("domainName")
    @Expose
    val domainName: String?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("fistDeposited")
    @Expose
    val fistDeposited: Boolean?,

    @SerializedName("mapping")
    @Expose
    val mapping: Mapping?,

    @SerializedName("playerInfoBean")
    @Expose
    val playerInfoBean: PlayerInfoBean?,

    @SerializedName("profileUpdate")
    @Expose
    val profileUpdate: Boolean?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?
) {
    class Mapping

    data class PlayerInfoBean(

        @SerializedName("addressLine1")
        @Expose
        val addressLine1: String?,

        @SerializedName("avatarPath")
        @Expose
        val avatarPath: String?,

        @SerializedName("commonContentPath")
        @Expose
        val commonContentPath: String?,

        @SerializedName("country")
        @Expose
        val country: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("currencyId")
        @Expose
        val currencyId: Int?,

        @SerializedName("dob")
        @Expose
        var dob: String?,

        @SerializedName("emailId")
        @Expose
        val emailId: String?,

        @SerializedName("emailVerified")
        @Expose
        val emailVerified: String?,

        @SerializedName("firstName")
        @Expose
        val firstName: String?,

        @SerializedName("gender")
        @Expose
        val gender: String?,

        @SerializedName("isOtpVerified")
        @Expose
        val isOtpVerified: Boolean?,

        @SerializedName("lastName")
        @Expose
        val lastName: String?,

        @SerializedName("mobileNo")
        @Expose
        val mobileNo: Long?,

        @SerializedName("mobileNumber")
        @Expose
        val mobileNumber: String?,

        @SerializedName("phoneVerified")
        @Expose
        val phoneVerified: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: Int?,

        @SerializedName("playerStatus")
        @Expose
        val playerStatus: String?,

        @SerializedName("sms")
        @Expose
        val sms: Boolean?,

        @SerializedName("userName")
        @Expose
        val userName: String?,

        @SerializedName("ussd")
        @Expose
        val ussd: Boolean?,

        @SerializedName("walletBean")
        @Expose
        val walletBean: WalletBean?
    ) {
        data class WalletBean(

            @SerializedName("bonusBalance")
            @Expose
            val bonusBalance: Int?,

            @SerializedName("cashBalance")
            @Expose
            val cashBalance: Double?,

            @SerializedName("depositBalance")
            @Expose
            val depositBalance: Int?,

            @SerializedName("practiceBalance")
            @Expose
            val practiceBalance: Int?,

            @SerializedName("totalBalance")
            @Expose
            val totalBalance: Double?,

            @SerializedName("totalDepositBalance")
            @Expose
            val totalDepositBalance: Int?,

            @SerializedName("totalWinningBalance")
            @Expose
            val totalWinningBalance: Double?,

            @SerializedName("totalWithdrawableBalance")
            @Expose
            val totalWithdrawableBalance: Int?,

            @SerializedName("winningBalance")
            @Expose
            val winningBalance: Int?,

            @SerializedName("withdrawableBal")
            @Expose
            val withdrawableBal: Double?
        )
    }
}