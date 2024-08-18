package com.skilrock.infinitylotoapp.viewmodels

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.LoginRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.repositories.LoginRepository
import com.skilrock.infinitylotoapp.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private var lastClickTime: Long = 0
    private var clickGap = 600

    var userName: String    = ""
    var password: String    = ""
    val liveDataLoader      = MutableLiveData<Boolean>()
    val liveDataValidation  = MutableLiveData<Validation>()
    val liveDataLoginStatus = MutableLiveData<ResponseStatus<LoginResponseData>>()
    val liveDataVibrator    = MutableLiveData<String>()

    sealed class Validation {
        data class UserNameValidation(val errorMessageCode: Int) : Validation()
        data class PasswordValidation(val errorMessageCode: Int) : Validation()
    }

    fun onLoginButtonClick(fcmToken: String) {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        liveDataVibrator.postValue("")
        if (validateInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = LoginRepository().performLogin(loginRequestData(fcmToken))
                    withContext(Dispatchers.Main) {
                        try {
                            when {
                                response.isSuccessful && response.body() != null -> {
                                    Log.i("log", "Login Response: ${Gson().toJson(response.body())}")
                                    onLoginResponse(response.body())
                                }
                                response.errorBody() != null -> {
                                    Log.e("log", "Login Response: $response")
                                    liveDataLoginStatus.postValue(ResponseStatus.Error(response.errorBody().toString()))
                                }
                                else -> {
                                    Log.e("log", "Login Response: $response")
                                    liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("log", "Login Response: $response")
                            liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    liveDataLoginStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onLoginResponse(response: LoginResponseData?) {
        if (response?.errorCode == 0)
            liveDataLoginStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataLoginStatus.postValue(ResponseStatus.Error(response?.respMsg ?: ""))
    }

    private fun validateInputs(): Boolean {
        if (userName.trim().isEmpty() || userName.trim().length < 8) {
            liveDataValidation.postValue(Validation.UserNameValidation(R.string.username_eight_characters))
            return false
        }

        if (password.trim().isEmpty() || password.trim().length < 8) {
            liveDataValidation.postValue(Validation.PasswordValidation(R.string.password_eight_characters))
            return false
        }

        if (!userName.trim().matches("(?!^[0-9]*\$)(?!^[a-zA-Z]*\$)^([a-zA-Z0-9]{6,15})\$".toRegex())) {
            liveDataValidation.postValue(Validation.UserNameValidation(R.string.username_must_be_combination_of_number_alphabets))
            return false
        }

        if (encryptedPassword(password).isEmpty()) {
            liveDataValidation.postValue(Validation.PasswordValidation(R.string.password_encrypt_error))
            return false
        }

        return true
    }

    private fun encryptedPassword(password: String) : String {
        val firstEncrypt = md5(password)
        return md5(firstEncrypt + LOGIN_TOKEN)
    }

    private fun loginRequestData(fcmToken: String) : LoginRequestData {
        return LoginRequestData(DEVICE_TYPE, BuildConfig.DOMAIN_NAME,
            LOGIN_DEVICE, LOGIN_TOKEN, encryptedPassword(password), REQUEST_IP, "",
            System.getProperty("http.agent"), userName, fcmToken)
    }

}