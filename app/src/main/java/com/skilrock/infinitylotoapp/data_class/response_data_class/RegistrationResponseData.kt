package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegistrationResponseData(

    @SerializedName("domainName")
    @Expose
    val domainName: String?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("playerLoginInfo")
    @Expose
    val playerLoginInfo: PlayerLoginInfo?,

    @SerializedName("playerToken")
    @Expose
    val playerToken: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?
) {
    data class PlayerLoginInfo(

        @SerializedName("addressVerified")
        @Expose
        val addressVerified: String?,

        @SerializedName("ageVerified")
        @Expose
        val ageVerified: String?,

        @SerializedName("avatarPath")
        @Expose
        val avatarPath: String?,

        @SerializedName("campaignName")
        @Expose
        val campaignName: String?,

        @SerializedName("commonContentPath")
        @Expose
        val commonContentPath: String?,

        @SerializedName("country")
        @Expose
        val country: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("emailVerified")
        @Expose
        val emailVerified: String?,

        @SerializedName("firstDepositDate")
        @Expose
        val firstDepositDate: String?,

        @SerializedName("firstLoginDate")
        @Expose
        val firstLoginDate: String?,

        @SerializedName("firstName")
        @Expose
        val firstName: String?,

        @SerializedName("isPlay2x")
        @Expose
        val isPlay2x: String?,

        @SerializedName("lastLoginDate")
        @Expose
        val lastLoginDate: String?,

        @SerializedName("lastName")
        @Expose
        val lastName: String?,

        @SerializedName("mobileNo")
        @Expose
        val mobileNo: String?,

        @SerializedName("olaPlayer")
        @Expose
        val olaPlayer: String?,

        @SerializedName("phoneVerified")
        @Expose
        val phoneVerified: String?,

        @SerializedName("playerId")
        @Expose
        val playerId: Int?,

        @SerializedName("playerStatus")
        @Expose
        val playerStatus: String?,

        @SerializedName("playerType")
        @Expose
        val playerType: String?,

        @SerializedName("referFriendCode")
        @Expose
        val referFriendCode: String?,

        @SerializedName("referSource")
        @Expose
        val referSource: String?,

        @SerializedName("regDevice")
        @Expose
        val regDevice: String?,

        @SerializedName("registrationDate")
        @Expose
        val registrationDate: String?,

        @SerializedName("registrationIp")
        @Expose
        val registrationIp: String?,

        @SerializedName("state")
        @Expose
        val state: String?,

        @SerializedName("unreadMsgCount")
        @Expose
        val unreadMsgCount: Int?,

        @SerializedName("userName")
        @Expose
        val userName: String?,

        @SerializedName("walletBean")
        @Expose
        val walletBean: WalletBean?
    ) {
        data class WalletBean(

            @SerializedName("bonusBalance")
            @Expose
            val bonusBalance: Double?,

            @SerializedName("cashBalance")
            @Expose
            val cashBalance: Double?,

            @SerializedName("currency")
            @Expose
            val currency: String?,

            @SerializedName("depositBalance")
            @Expose
            val depositBalance: Double?,

            @SerializedName("practiceBalance")
            @Expose
            val practiceBalance: Double?,

            @SerializedName("preferredWallet")
            @Expose
            val preferredWallet: String?,

            @SerializedName("totalBalance")
            @Expose
            val totalBalance: Double?,

            @SerializedName("winningBalance")
            @Expose
            val winningBalance: Double?,

            @SerializedName("withdrawableBal")
            @Expose
            val withdrawableBal: Double?
        )
    }
}