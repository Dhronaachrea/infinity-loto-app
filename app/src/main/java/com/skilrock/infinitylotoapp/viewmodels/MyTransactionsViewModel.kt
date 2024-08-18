package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.MyTransactionRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTransactionResponseData
import com.skilrock.infinitylotoapp.repositories.MyTransactionRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import com.skilrock.infinitylotoapp.utility.getCurrentDate
import com.skilrock.infinitylotoapp.utility.getPreviousDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTransactionsViewModel : BaseViewModel() {

    private var txnId       = "ALL"
    var txnAll              = "ALL"
    var txnWager            = "PLR_WAGER"
    var txnDeposit          = "PLR_DEPOSIT"
    var txnWithdrawal       = "PRL_WITHDRAWAL"
    var txnWinning          = "PLR_WINNING"
    var txnTransferIn       = "TRANSFER_IN"
    var txnTransferOut      = "TRANSFER_OUT"

    var startDate: String   = getPreviousDate(30)
    var endDate: String     = getCurrentDate()
    val activityTitle       = ObservableInt(R.string.my_transactions)

    val liveDataTxnType     = MutableLiveData<String>()
    val liveDataResponse    = MutableLiveData<ResponseStatus<MyTransactionResponseData>>()

    fun onTxnTypeClick(id: String) {
        liveDataVibrator.postValue("")
        liveDataTxnType.postValue(id)
        txnId = id
        onProceedClick()
    }

    fun onProceedClick() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = MyTransactionRepository().callApi(requestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "MyTransactions Response: ${Gson().toJson(response.body())}")
                                onMyTransactionsResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "MyTransactions Response: $response")
                                liveDataResponse.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "MyTransactions Response: $response")
                                liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "MyTransactions Response: $response")
                        liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onMyTransactionsResponse(response: MyTransactionResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataResponse.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }

    private val requestData:() -> MyTransactionRequestData = {
        MyTransactionRequestData(fromDate = startDate.replace("-", "/"),
            toDate = endDate.trim().replace("-", "/"), txnType = txnId)
    }
}