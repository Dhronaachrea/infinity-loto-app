package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.AddNewAccountRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.AddNewAccountResponseData
import com.skilrock.infinitylotoapp.repositories.DepositRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewAccountViewModel : BaseViewModel() {

    val liveDataAddNewAccResponse   = MutableLiveData<ResponseStatus<AddNewAccountResponseData>>()
    val liveDataOtpResponse         = MutableLiveData<ResponseStatus<AddNewAccountResponseData>>()

    fun callAddNewAccountApi(requestData: AddNewAccountRequestData) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DepositRepository().callAddNewAccountApi(requestData)
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Add New Account Response: ${Gson().toJson(response.body())}")
                                onAddNewAccountResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Add New Account Response: $response")
                                liveDataAddNewAccResponse.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Add New Account Response: $response")
                                liveDataAddNewAccResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Add New Account Response: $response")
                        liveDataAddNewAccResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataAddNewAccResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onAddNewAccountResponse(response: AddNewAccountResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataAddNewAccResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataAddNewAccResponse.postValue(ResponseStatus.Error(it.errorMsg ?: "", it.errorCode))
        }
    }

    fun callOtpApi(requestData: AddNewAccountRequestData) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = DepositRepository().callAddNewAccountApi(requestData)
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "OTP Response: ${Gson().toJson(response.body())}")
                                onOtpResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "OTP Response: $response")
                                liveDataOtpResponse.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "OTP Response: $response")
                                liveDataOtpResponse.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "OTP Response: $response")
                        liveDataOtpResponse.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataOtpResponse.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onOtpResponse(response: AddNewAccountResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataOtpResponse.postValue(ResponseStatus.Success(response))
            else
                liveDataOtpResponse.postValue(ResponseStatus.Error(it.errorMsg ?: "", it.errorCode))
        }
    }

}