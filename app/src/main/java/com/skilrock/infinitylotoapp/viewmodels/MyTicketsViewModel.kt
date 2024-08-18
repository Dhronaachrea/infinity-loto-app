package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.MyTicketsRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTicketsResponseData
import com.skilrock.infinitylotoapp.repositories.MyTicketsRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import com.skilrock.infinitylotoapp.utility.getCurrentDate
import com.skilrock.infinitylotoapp.utility.getPreviousDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTicketsViewModel : BaseViewModel() {

    var startDate: String   = getPreviousDate(30)
    var endDate: String     = getCurrentDate()
    val activityTitle       = ObservableInt(R.string.my_tickets)

    val liveDataResponse    = MutableLiveData<ResponseStatus<MyTicketsResponseData>>()

    fun onProceedClick() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = MyTicketsRepository().callApi(requestData())
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

    private fun onMyTransactionsResponse(response: MyTicketsResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataResponse.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }

    private val requestData:() -> MyTicketsRequestData = {
        MyTicketsRequestData(fromDate = startDate.replace("-", "/"),
            toDate = endDate.trim().replace("-", "/"))
    }
}