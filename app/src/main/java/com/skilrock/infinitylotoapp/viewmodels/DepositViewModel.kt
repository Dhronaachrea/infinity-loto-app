package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.BalanceRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.DepositPendingRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.DepositUiRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositPendingResponseData
import com.skilrock.infinitylotoapp.repositories.DepositRepository
import com.skilrock.infinitylotoapp.repositories.ProfileRepository
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DepositViewModel : BaseViewModel() {

    val liveDataDepositUi           = MutableLiveData<ResponseStatus<JSONObject>>()
    val liveDataPendingResponse     = MutableLiveData<ResponseStatus<DepositPendingResponseData>>()
    val liveDataBalanceStatus       = MutableLiveData<ResponseStatus<BalanceResponseData>>()

    fun callUiApi() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DepositRepository().callUiApi(DepositUiRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                withContext(Dispatchers.IO) {
                                    val stringResponse: String? = response.body()?.string()
                                    stringResponse?.let { res ->
                                        Log.i("log", "Deposit UI Response: $res")
                                        val jsonResponse = JSONObject(res)
                                        onDepositUiResponse(jsonResponse)
                                    } ?: run {
                                        Log.e("log", "Deposit UI Response: $response")
                                        liveDataDepositUi.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                                    }
                                }
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Deposit UI Response: $response")
                                liveDataDepositUi.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Deposit UI Response: $response")
                                liveDataDepositUi.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Deposit UI Response: $response")
                        liveDataDepositUi.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataDepositUi.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onDepositUiResponse(response: JSONObject) {
        if (response.optInt("errorCode") == 0)
            liveDataDepositUi.postValue(ResponseStatus.Success(response))
        else
            liveDataDepositUi.postValue(ResponseStatus.Error(response.optString("errorMessage") ?: "", response.optInt("errorCode")))
    }

    fun callDepositPendingApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DepositRepository().callDepositPendingApi(DepositPendingRequestData(txnType="DEPOSIT"))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Deposit Pending Response: ${Gson().toJson(response.body())}")
                                onMyTransactionsResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Deposit Pending Response: $response")
                                liveDataPendingResponse.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Deposit Pending Response: $response")
                                liveDataPendingResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Deposit Pending Response: $response")
                        liveDataPendingResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataPendingResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onMyTransactionsResponse(response: DepositPendingResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataPendingResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataPendingResponse.postValue(ResponseStatus.Error(it.errorMessage ?: "", it.errorCode))
        }
    }

    fun callBalanceApi() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

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