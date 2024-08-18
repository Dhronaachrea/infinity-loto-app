package com.skilrock.infinitylotoapp.utility

import android.content.Context
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import java.util.*

object PlayerInfo {

    private var loginData: LoginResponseData? = null

    fun setLoginData(loginResponse: LoginResponseData) {
        loginData = loginResponse
    }

    fun destroy() {
        loginData = null
    }

    fun setLoginData(context: Context, loginResponse: LoginResponseData) {
        loginData = loginResponse
        SharedPrefUtils.setInt(context, SharedPrefUtils.PLAYER_ID, loginResponse.playerLoginInfo?.playerId ?: 0)
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_TOKEN, loginResponse.playerToken ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_USER_NAME, loginResponse.playerLoginInfo?.userName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_MOBILE_NUMBER, loginResponse.playerLoginInfo?.mobileNo ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_FIRST_NAME, loginResponse.playerLoginInfo?.firstName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_LAST_NAME, loginResponse.playerLoginInfo?.lastName ?: "")
        SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginResponse))
    }

    fun setBalance(context: Context, wallet: BalanceResponseData.Wallet) {
        loginData?.playerLoginInfo?.walletBean?.let {
            it.bonusBalance             = wallet.bonusBalance
            it.cashBalance              = wallet.cashBalance
            it.currency                 = wallet.currency
            it.depositBalance           = wallet.depositBalance
            it.practiceBalance          = wallet.practiceBalance
            it.preferredWallet          = wallet.preferredWallet
            it.totalBalance             = wallet.totalBalance
            it.totalDepositBalance      = wallet.totalDepositBalance
            it.totalWithdrawableBalance = wallet.totalWithdrawableBalance
            it.winningBalance           = wallet.winningBalance
            it.withdrawableBal          = wallet.withdrawableBal
            it.totalWinningBalance      = wallet.totalWinningBalance
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun updateProfile(context: Context, firstName: String, lastName: String, email: String, dob: String, gender: String, address: String) {
        loginData?.playerLoginInfo?.let { profile ->
            profile.firstName       = firstName
            profile.lastName        = lastName
            profile.emailId         = email
            profile.dob             = dob
            profile.gender          = gender
            profile.addressLine1    = address
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun setTotalBalance(context: Context, totalBalance: Double) {
        loginData?.playerLoginInfo?.walletBean?.let {
            it.totalBalance = totalBalance
            SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
        }
    }

    fun setProfileImage(context: Context, path: String) {
        loginData?.playerLoginInfo?.avatarPath = path
        //SharedPrefUtils.setString(context, SharedPrefUtils.PLAYER_DATA, Gson().toJson(loginData))
    }

    fun getPlayerId() : Int = loginData?.playerLoginInfo?.playerId ?: 0
    fun getPlayerToken() : String = loginData?.playerToken ?: ""
    fun getPlayerUserName() : String = loginData?.playerLoginInfo?.userName ?: ""
    fun getPlayerMobileNumber() : String = loginData?.playerLoginInfo?.mobileNo ?: ""
    fun getPlayerFirstName() : String = loginData?.playerLoginInfo?.firstName?.capitalize(Locale.ROOT) ?: ""
    fun getPlayerEmailId() : String = loginData?.playerLoginInfo?.emailId ?: ""
    fun getPlayerDob() : String = loginData?.playerLoginInfo?.dob ?: ""
    fun getPlayerAddress() : String = loginData?.playerLoginInfo?.addressLine1 ?: ""
    fun getPlayerCountry() : String = loginData?.playerLoginInfo?.country?.capitalize(Locale.ROOT) ?: ""
    fun getPlayerLastName() : String = loginData?.playerLoginInfo?.lastName?.capitalize(Locale.ROOT) ?: ""
    fun getPlayerTotalBalance() : String = loginData?.playerLoginInfo?.walletBean?.totalBalance?.toString() ?: "0"
    fun getProfilePicPath() : String = loginData?.playerLoginInfo?.commonContentPath + loginData?.playerLoginInfo?.avatarPath.toString()
    fun getCurrency() : String = loginData?.playerLoginInfo?.walletBean?.currency ?: BuildConfig.CURRENCY
    fun getCurrencyDisplayCode() : String = loginData?.playerLoginInfo?.walletBean?.currencyDisplayCode ?: BuildConfig.CURRENCY_DISPLAY_CODE
    fun getPlayerInfo() : LoginResponseData? = loginData

    fun isEmailVerified() : Boolean {
        loginData?.playerLoginInfo?.emailVerified?.let {
            return !(it.equals("N", true) || it.equals("NO", true))
        } ?: return true
    }

    fun setEmailVerified() {
        loginData?.playerLoginInfo?.emailVerified = "Y"
    }

    fun getPlayerGender() : String {
        loginData?.playerLoginInfo?.gender?.let {
            return when (it) {
                "M" -> "Male"
                "F" -> "Female"
                else -> it
            }
        } ?: return ""
    }

}