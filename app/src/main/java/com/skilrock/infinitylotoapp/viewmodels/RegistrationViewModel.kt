package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.RegistrationOtpRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.RegistrationRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.RegistrationOtpResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.RegistrationResponseData
import com.skilrock.infinitylotoapp.repositories.RegistrationRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel : ViewModel() {

    var firstName: String           = ""
    var lastName: String            = ""
    var userName: String            = ""
    var mobileNumber: String        = ""
    var password: String            = ""
    var confirmPassword: String     = ""
    var referCode: String           = ""
    val liveDataLoader              = MutableLiveData<Boolean>()
    val liveDataVibrator            = MutableLiveData<String>()
    val liveDataValidation          = MutableLiveData<Validation>()
    val liveDataHideKeyboard        = MutableLiveData<Boolean>()
    val liveDataRegistrationStatus  = MutableLiveData<ResponseStatus<RegistrationResponseData>>()
    val liveDataRegistrationOtp     = MutableLiveData<ResponseStatus<RegistrationOtpResponseData>>()

    sealed class Validation {
        data class FirstName(val errorMessageCode: Int) : Validation()
        data class LastName(val errorMessageCode: Int) : Validation()
        data class UserName(val errorMessageCode: Int) : Validation()
        data class MobileNumber(val errorMessageCode: Int) : Validation()
        data class Password(val errorMessageCode: Int) : Validation()
        data class ConfirmPassword(val errorMessageCode: Int) : Validation()
    }

    fun onRegisterButtonClick(otp: String) {
        liveDataHideKeyboard.postValue(true)
        liveDataVibrator.postValue("")
        if (validateInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response =
                        RegistrationRepository().registerPlayer(registrationRequestData(otp))
                    withContext(Dispatchers.Main) {
                        try {
                            when {
                                response.isSuccessful && response.body() != null -> {
                                    Log.i("log", "Registration Response: ${Gson().toJson(response.body())}")
                                    onRegistrationResponse(response.body())
                                }
                                response.errorBody() != null -> {
                                    Log.e("log", "Registration Response: $response")
                                    liveDataRegistrationStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))
                                }
                                else -> {
                                    Log.e("log", "Registration Response: $response")
                                    liveDataRegistrationStatus.postValue(
                                        ResponseStatus.TechnicalError(R.string.some_technical_issue)
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("log", "Registration Response: $response")
                            liveDataRegistrationStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    liveDataRegistrationStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onRegistrationResponse(response: RegistrationResponseData?) {
        if (response?.errorCode == 0)
            liveDataRegistrationStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataRegistrationStatus.postValue(ResponseStatus.Error(response?.respMsg ?: ""))
    }

    private fun validateInputs() : Boolean {
        if (userName.trim().isEmpty() || userName.trim().length < 8) {
            liveDataValidation.postValue(Validation.UserName(R.string.username_eight_characters))
            return false
        }

        if (!userName.trim().matches("(?!^[0-9]*\$)(?!^[a-zA-Z]*\$)^([a-zA-Z0-9]{6,16})\$".toRegex())) {
            liveDataValidation.postValue(Validation.UserName(R.string.username_must_be_combination_of_number_alphabets))
            return false
        }

        if (password.trim().isEmpty() || password.trim().length < 8) {
            liveDataValidation.postValue(Validation.Password(R.string.password_eight_characters))
            return false
        }

        if (password != confirmPassword) {
            liveDataValidation.postValue(Validation.ConfirmPassword(R.string.passwords_do_not_match))
            return false
        }

        if (mobileNumber.trim().isEmpty()) {
            liveDataValidation.postValue(Validation.MobileNumber(R.string.enter_mobile_number))
            return false
        }

        if (mobileNumber.trim().length < BuildConfig.MOBILE_NO_LENGTH) {
            liveDataValidation.postValue(Validation.MobileNumber(R.string.enter_valid_mobile_number))
            return false
        }

        if (mobileNumber.trim().take(1) != "7") {
            liveDataValidation.postValue(Validation.MobileNumber(R.string.enter_valid_mobile_number))
            return false
        }

        return true
    }

    private val registrationRequestData:(otp: String) -> RegistrationRequestData = {
        RegistrationRequestData(firstName = firstName, lastName= lastName, mobileNo = mobileNumber,
            password = password, userName = userName, referCode = referCode, otp = it)
    }

    fun callRegistrationOtpApi(showResendOtpToast: Boolean = false) {
        liveDataHideKeyboard.postValue(true)
        if (validateInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = RegistrationRepository().registrationOtp(registrationRequestOtpData())
                    withContext(Dispatchers.Main) {
                        try {
                            when {
                                response.isSuccessful && response.body() != null -> {
                                    Log.i("log", "Registration Otp Response: ${Gson().toJson(response.body())}")
                                    onRegistrationOtpResponse(response.body(), showResendOtpToast)
                                }
                                response.errorBody() != null -> {
                                    Log.e("log", "Registration Otp Response: $response")
                                    liveDataRegistrationOtp.postValue(ResponseStatus.Error(response.errorBody().toString()))
                                }
                                else -> {
                                    Log.e("log", "Registration Otp Response: $response")
                                    liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("log", "Registration Otp Response: $response")
                            liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    liveDataRegistrationOtp.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onRegistrationOtpResponse(response: RegistrationOtpResponseData?, showResendOtpToast: Boolean) {
        if (response?.errorCode == 0) {
            response.showResendOtpToast = showResendOtpToast
            liveDataRegistrationOtp.postValue(ResponseStatus.Success(response))
        }
        else
            liveDataRegistrationOtp.postValue(ResponseStatus.Error(response?.respMsg ?: ""))
    }

    private val registrationRequestOtpData:() -> RegistrationOtpRequestData = {

        RegistrationOtpRequestData(firstName = firstName, lastName= lastName, mobileNo = mobileNumber.toLong(),
            password = password, userName = mobileNumber, referCode = referCode, emailId = "")
    }

}