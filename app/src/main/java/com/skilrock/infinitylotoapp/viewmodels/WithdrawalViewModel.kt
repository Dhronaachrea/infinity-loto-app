package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.BalanceRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.DepositPendingRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.WithdrawalRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.WithdrawalUiRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositPendingResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.WithdrawResponseData
import com.skilrock.infinitylotoapp.repositories.ProfileRepository
import com.skilrock.infinitylotoapp.repositories.WithdrawalRepository
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WithdrawalViewModel : BaseViewModel() {

    val liveDataWithdrawalUi                = MutableLiveData<ResponseStatus<JSONObject>>()
    val liveDataWithdrawalPendingResponse   = MutableLiveData<ResponseStatus<DepositPendingResponseData>>()
    val liveDataWithdrawResponse            = MutableLiveData<ResponseStatus<WithdrawResponseData>>()
    val liveDataBalanceStatus               = MutableLiveData<ResponseStatus<BalanceResponseData>>()

    fun callUiApi() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = WithdrawalRepository().callUiApi(WithdrawalUiRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                withContext(Dispatchers.IO) {
                                    val stringResponse: String? = response.body()?.string()
                                    stringResponse?.let { res ->
                                        Log.i("log", "Withdrawal UI Response: $res")
                                        val jsonResponse = JSONObject(res)
                                        onDepositUiResponse(jsonResponse)
                                    } ?: run {
                                        Log.e("log", "Withdrawal UI Response: $response")
                                        liveDataWithdrawalUi.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                                    }
                                }
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Withdrawal UI Response: $response")
                                liveDataWithdrawalUi.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Withdrawal UI Response: $response")
                                liveDataWithdrawalUi.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Withdrawal UI Response: $response")
                        liveDataWithdrawalUi.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataWithdrawalUi.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onDepositUiResponse(response: JSONObject) {
        if (response.optInt("errorCode") == 0)
            liveDataWithdrawalUi.postValue(ResponseStatus.Success(response))
        else
            liveDataWithdrawalUi.postValue(ResponseStatus.Error(response.optString("errorMessage") ?: "", response.optInt("errorCode")))
    }

    fun callWithdrawalPendingApi() {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = WithdrawalRepository().callDepositPendingApi(DepositPendingRequestData(txnType="WITHDRAWAL"))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Withdrawal Pending Response: ${Gson().toJson(response.body())}")
                                onPendingTransactionsResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Withdrawal Pending Response: $response")
                                liveDataWithdrawalPendingResponse.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Withdrawal Pending Response: $response")
                                liveDataWithdrawalPendingResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Withdrawal Pending Response: $response")
                        liveDataWithdrawalPendingResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataWithdrawalPendingResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onPendingTransactionsResponse(response: DepositPendingResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataWithdrawalPendingResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataWithdrawalPendingResponse.postValue(ResponseStatus.Error(it.errorMessage ?: "", it.errorCode))
        }
    }

    fun callWithdrawApi(withdrawalRequestData: WithdrawalRequestData) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = WithdrawalRepository().callWithdrawRequestApi(withdrawalRequestData)
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Withdraw Response: ${Gson().toJson(response.body())}")
                                onWithdrawResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Withdraw Response: $response")
                                liveDataWithdrawResponse.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Withdraw Response: $response")
                                liveDataWithdrawResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Withdraw Response: $response")
                        liveDataWithdrawResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataWithdrawResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onWithdrawResponse(response: WithdrawResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataWithdrawResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataWithdrawResponse.postValue(ResponseStatus.Error(it.errorMsg ?: "", it.errorCode))
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
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
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