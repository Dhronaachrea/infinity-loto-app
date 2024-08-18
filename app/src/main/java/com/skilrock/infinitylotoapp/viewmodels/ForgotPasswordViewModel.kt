package com.skilrock.infinitylotoapp.viewmodels

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.ForgotPasswordRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.ForgotPasswordResponseData
import com.skilrock.infinitylotoapp.repositories.ForgotPasswordRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel : ViewModel() {

    private var lastClickTime: Long     = 0
    private var clickGap                = 600
    var mobileNumber: String            = ""
    val liveDataVibrator                = MutableLiveData<String>()
    val liveDataLoader                  = MutableLiveData<Boolean>()
    val liveDataValidateMobileNumber    = MutableLiveData<Int>()
    val liveDataOtpStatus               = MutableLiveData<ResponseStatus<ForgotPasswordResponseData>>()
    val liveDataResetPasswordStatus     = MutableLiveData<ResponseStatus<ForgotPasswordResponseData>>()

    fun sendOtp() {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        liveDataVibrator.postValue("")
        if (validateMobileNumber()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response =
                        ForgotPasswordRepository().sendOtp(ForgotPasswordRequestData(mobileNo = mobileNumber))
                    withContext(Dispatchers.Main) {
                        try {
                            when {
                                response.isSuccessful && response.body() != null -> {
                                    Log.i("log", "OTP Response: ${Gson().toJson(response.body())}")
                                    onOtpResponse(response.body())
                                }
                                response.errorBody() != null -> {
                                    Log.e("log", "OTP Response: $response")
                                    liveDataOtpStatus.postValue(
                                        ResponseStatus.Error(
                                            response.errorBody().toString()
                                        )
                                    )
                                }
                                else -> {
                                    Log.e("log", "OTP Response: $response")
                                    liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("log", "OTP Response: $response")
                            liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onOtpResponse(response: ForgotPasswordResponseData?) {
        if (response?.errorCode == 0)
            liveDataOtpStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataOtpStatus.postValue(ResponseStatus.Error(response?.respMsg ?: ""))
    }

    private fun validateMobileNumber() : Boolean {
        if (mobileNumber.trim().isEmpty()) {
            liveDataValidateMobileNumber.postValue(R.string.enter_valid_mobile_number_or_username)
            return false
        }

        if (mobileNumber.trim().length < 8) {
            liveDataValidateMobileNumber.postValue(R.string.enter_valid_mobile_number_or_username)
            return false
        }

        return true
    }

    fun resetPassword(otp: String, newPassword: String,
                      confirmPassword: String, mobileNumber: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ForgotPasswordRepository().resetPassword(
                    getForgotPasswordRequestData(
                        otp,
                        newPassword,
                        confirmPassword,
                        mobileNumber
                    )
                )
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Reset Response: ${Gson().toJson(response.body())}")
                                onResetPasswordResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Reset Response: $response")
                                liveDataResetPasswordStatus.postValue(
                                    ResponseStatus.Error(
                                        response.errorBody().toString()
                                    )
                                )
                            }
                            else -> {
                                Log.e("log", "Reset Response: $response")
                                liveDataResetPasswordStatus.postValue(
                                    ResponseStatus.TechnicalError(
                                        R.string.some_technical_issue
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Reset Response: $response")
                        liveDataResetPasswordStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataOtpStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onResetPasswordResponse(response: ForgotPasswordResponseData?) {
        if (response?.errorCode == 0)
            liveDataResetPasswordStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataResetPasswordStatus.postValue(ResponseStatus.Error(response?.respMsg ?: ""))
    }

    private fun getForgotPasswordRequestData(otpParam: String, newPasswordParam: String,
        confirmPasswordParam: String, mobileNumber: String) : ForgotPasswordRequestData  {
            return ForgotPasswordRequestData(otp = otpParam, newPassword = newPasswordParam,
                confirmPassword = confirmPasswordParam, mobileNo = mobileNumber)
    }

}