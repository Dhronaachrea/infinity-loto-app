package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponseData(

    @SerializedName("domainName")
    @Expose
    val domainName: String?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("firstDepositCampTrackId")
    @Expose
    val firstDepositCampTrackId: Int?,

    @SerializedName("firstDepositReferSource")
    @Expose
    val firstDepositReferSource: String?,

    @SerializedName("firstDepositReferSourceId")
    @Expose
    val firstDepositReferSourceId: Int?,

    @SerializedName("firstDepositSubSourceId")
    @Expose
    val firstDepositSubSourceId: Int?,

    @SerializedName("mapping")
    @Expose
    val mapping: Mapping?,

    @SerializedName("playerLoginInfo")
    @Expose
    val playerLoginInfo: PlayerLoginInfo?,

    @SerializedName("playerToken")
    @Expose
    val playerToken: String?,

    @SerializedName("rummyDeepLink")
    @Expose
    val rummyDeepLink: String?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?
) {
    class Mapping

    data class PlayerLoginInfo(

        @SerializedName("addressLine1")
        @Expose
        var addressLine1: String?,

        @SerializedName("addressVerified")
        @Expose
        val addressVerified: String?,

        @SerializedName("affilateId")
        @Expose
        val affilateId: Int?,

        @SerializedName("ageVerified")
        @Expose
        val ageVerified: String?,

        @SerializedName("autoPassword")
        @Expose
        val autoPassword: String?,

        @SerializedName("avatarPath")
        @Expose
        var avatarPath: String?,

        @SerializedName("commonContentPath")
        @Expose
        val commonContentPath: String?,

        @SerializedName("completeImagePath")
        @Expose
        val completeImagePath: String?,

        @SerializedName("country")
        @Expose
        val country: String?,

        @SerializedName("countryCode")
        @Expose
        val countryCode: String?,

        @SerializedName("dob")
        @Expose
        var dob: String?,

        @SerializedName("emailId")
        @Expose
        var emailId: String?,

        @SerializedName("emailVerified")
        @Expose
        var emailVerified: String?,

        @SerializedName("firstDepositDate")
        @Expose
        val firstDepositDate: String?,

        @SerializedName("firstLoginDate")
        @Expose
        val firstLoginDate: String?,

        @SerializedName("firstName")
        @Expose
        var firstName: String?,

        @SerializedName("gender")
        @Expose
        var gender: String?,

        @SerializedName("isPlay2x")
        @Expose
        val isPlay2x: String?,

        @SerializedName("lastLoginDate")
        @Expose
        val lastLoginDate: String?,

        @SerializedName("lastLoginIP")
        @Expose
        val lastLoginIP: String?,

        @SerializedName("lastName")
        @Expose
        var lastName: String?,

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
            var bonusBalance: Double?,

            @SerializedName("cashBalance")
            @Expose
            var cashBalance: Double?,

            @SerializedName("currency")
            @Expose
            var currency: String?,

            @SerializedName("currencyDisplayCode")
            @Expose
            var currencyDisplayCode: String?,

            @SerializedName("depositBalance")
            @Expose
            var depositBalance: Double?,

            @SerializedName("practiceBalance")
            @Expose
            var practiceBalance: Double?,

            @SerializedName("preferredWallet")
            @Expose
            var preferredWallet: String?,

            @SerializedName("totalBalance")
            @Expose
            var totalBalance: Double?,

            @SerializedName("totalDepositBalance")
            @Expose
            var totalDepositBalance: Double?,

            @SerializedName("totalWithdrawableBalance")
            @Expose
            var totalWithdrawableBalance: Double?,

            @SerializedName("winningBalance")
            @Expose
            var winningBalance: Double?,

            @SerializedName("withdrawableBal")
            @Expose
            var withdrawableBal: Double?,

            @SerializedName("totalWinningBalance")
            @Expose
            var totalWinningBalance: Double?
        )
    }
}