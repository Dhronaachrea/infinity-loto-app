package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.VersionRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.VersionResponseData
import com.skilrock.infinitylotoapp.repositories.SplashRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel : ViewModel() {

    val liveDataVersionStatus = MutableLiveData<ResponseStatus<VersionResponseData>>()

    fun callVersionApi(appVersion: String, playerToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = SplashRepository().callVersionApi(getRequestData(appVersion, playerToken))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Version Response: ${Gson().toJson(response.body())}")
                                onVersionResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Version Response: $response")
                                liveDataVersionStatus.postValue(
                                    ResponseStatus.Error(
                                        response.errorBody().toString()
                                    )
                                )
                            }
                            else -> {
                                Log.e("log", "Version Response: $response")
                                liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Version Response: $response")
                        liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataVersionStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onVersionResponse(response: VersionResponseData?) {
        if (response?.errorCode == 0)
            liveDataVersionStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataVersionStatus.postValue(ResponseStatus.Error(response?.respMsg ?: ""))
    }

    private fun getRequestData(appVersion: String, plrToken: String) : VersionRequestData {
        return VersionRequestData(currAppVer = appVersion, playerToken = plrToken)
    }

}