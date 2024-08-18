package com.skilrock.infinitylotoapp.data_class.response_data_class


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VersionResponseData(

    @SerializedName("appDetails")
    @Expose
    val appDetails: AppDetails?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int?,

    @SerializedName("gameEngineInfo")
    @Expose
    val gameEngineInfo: GameEngineInfo?,

    @SerializedName("isplayerLogin")
    @Expose
    val isplayerLogin: Boolean?,

    @SerializedName("profile")
    @Expose
    val profile: Profile?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("staticLogoDisplay")
    @Expose
    val staticLogoDisplay: Boolean?
) {
    data class AppDetails(

        @SerializedName("isUpdateAvailable")
        @Expose
        val isUpdateAvailable: Boolean?,

        @SerializedName("mandatory")
        @Expose
        val mandatory: Boolean?,

        @SerializedName("message")
        @Expose
        val message: String?,

        @SerializedName("version_type")
        @Expose
        val versionType: String?,

        @SerializedName("url")
        @Expose
        val url: String?,

        @SerializedName("versionDate")
        @Expose
        val versionDate: String?,

        @SerializedName("versionCode")
        @Expose
        val versionCode: String?,

        @SerializedName("version")
        @Expose
        val version: String?,

        @SerializedName("os")
        @Expose
        val os: String?,

        @SerializedName("appType")
        @Expose
        val appType: String?
    )

    data class GameEngineInfo(

        @SerializedName("DGE")
        @Expose
        val dGE: DGE?,

        @SerializedName("IGE")
        @Expose
        val iGE: IGE?,

        @SerializedName("SBS")
        @Expose
        val sBS: SBS?,

        @SerializedName("SLE")
        @Expose
        val sLE: SLE?,

        @SerializedName("BETGAMES")
        @Expose
        val bETGAMES: BETGAMES?,

        @SerializedName("VIRTUAL_GAMES")
        @Expose
        val vIRTUALGAMES: BETGAMES?
    ) {
        data class DGE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class IGE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class SBS(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class SLE(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )

        data class BETGAMES(

            @SerializedName("serverUrl")
            @Expose
            val serverUrl: String?
        )
    }

    data class Profile(

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
        val playerToken: String?
    ) {
        data class PlayerLoginInfo(

            @SerializedName("addressVerified")
            @Expose
            val addressVerified: String?,

            @SerializedName("affilateId")
            @Expose
            val affilateId: Int?,

            @SerializedName("ageVerified")
            @Expose
            val ageVerified: String?,

            @SerializedName("avatarPath")
            @Expose
            val avatarPath: String?,

            @SerializedName("commonContentPath")
            @Expose
            val commonContentPath: String?,

            @SerializedName("country")
            @Expose
            val country: String?,

            @SerializedName("dob")
            @Expose
            val dob: String?,

            @SerializedName("emailId")
            @Expose
            val emailId: String?,

            @SerializedName("emailVerified")
            @Expose
            val emailVerified: String?,

            @SerializedName("firstDepositDate")
            @Expose
            val firstDepositDate: String?,

            @SerializedName("firstName")
            @Expose
            val firstName: String?,

            @SerializedName("gender")
            @Expose
            val gender: String?,

            @SerializedName("isPlay2x")
            @Expose
            val isPlay2x: String?,

            @SerializedName("lastLoginIP")
            @Expose
            val lastLoginIP: String?,

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
}