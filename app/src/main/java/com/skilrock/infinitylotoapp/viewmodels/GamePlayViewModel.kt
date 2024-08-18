package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.BalanceRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.repositories.ProfileRepository
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GamePlayViewModel : BaseViewModel() {

    val liveDataBalanceStatus = MutableLiveData<ResponseStatus<BalanceResponseData>>()

    fun onBalanceRefreshClick() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callBalanceApi(
                    BalanceRequestData(
                        playerId = PlayerInfo.getPlayerId().toString(),
                        playerToken = PlayerInfo.getPlayerToken()
                    )
                )
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Balance Response: ${Gson().toJson(response.body())}")
                                onBalanceResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Balance Response: $response")
                                liveDataBalanceStatus.postValue(
                                    ResponseStatus.Error(
                                        response.errorBody().toString(), -1
                                    )
                                )
                            }
                            else -> {
                                Log.e("log", "Balance Response: $response")
                                liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Balance Response: $response")
                        liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    } finally {
                        liveDataLoader.postValue(false)
                    }
                }
            } catch (e: Exception) {
                liveDataBalanceStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onBalanceResponse(response: BalanceResponseData?) {
        response?.let {
            if (it.errorCode == 0) {
                playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} ${it.wallet?.totalBalance}"
                liveDataBalanceStatus.postValue(ResponseStatus.Success(response))
            } else
                liveDataBalanceStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }
}