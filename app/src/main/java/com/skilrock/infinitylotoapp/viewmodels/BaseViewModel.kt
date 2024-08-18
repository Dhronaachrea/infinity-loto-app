package com.skilrock.infinitylotoapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skilrock.infinitylotoapp.utility.PlayerInfo


abstract class BaseViewModel : ViewModel() {

    var playerName = MutableLiveData(
        if (PlayerInfo.getPlayerFirstName().trim().isEmpty()) {
            PlayerInfo.getPlayerUserName()
        } else {
            PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
        }
    )

    var playerBalance =
        MutableLiveData("${PlayerInfo.getCurrencyDisplayCode()} ${PlayerInfo.getPlayerTotalBalance()}")

    val liveDataLoader = MutableLiveData<Boolean>()
    val liveDataVibrator = MutableLiveData<String>()
}
