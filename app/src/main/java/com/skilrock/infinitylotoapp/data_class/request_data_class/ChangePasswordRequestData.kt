package com.skilrock.infinitylotoapp.data_class.request_data_class

import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.utility.PlayerInfo

data class ChangePasswordRequestData(
    val domainName: String? = BuildConfig.DOMAIN_NAME,
    val newPassword: String? = "",
    val oldPassword: String? = "",
    val playerId: Int? = PlayerInfo.getPlayerId(),
    val playerToken: String? = PlayerInfo.getPlayerToken()
)