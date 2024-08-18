package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.ChangePasswordRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.ChangePasswordResponseData
import com.skilrock.infinitylotoapp.repositories.ChangePasswordRepository
import com.skilrock.infinitylotoapp.utility.ChangePasswordValidation
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordViewModel : BaseViewModel() {

    var oldPassword: String         = ""
    var newPassword: String         = ""
    var confirmPassword: String     = ""

    val activityTitle               = ObservableInt(R.string.change_password)

    val liveDataValidation          = MutableLiveData<ChangePasswordValidation>()
    val liveDataResponseStatus      = MutableLiveData<ResponseStatus<ChangePasswordResponseData>>()

    private fun validateInputs(): Boolean {
        if (oldPassword.trim().isBlank() || oldPassword.trim().length < 8) {
            liveDataValidation.postValue(ChangePasswordValidation.OldPasswordValidation(R.string.password_must_be_of_min_eight_digits))
            return false
        }

        if (newPassword.trim().isBlank() || newPassword.trim().length < 8) {
            liveDataValidation.postValue(ChangePasswordValidation.NewPasswordValidation(R.string.password_must_be_of_min_eight_digits))
            return false
        }

        if (confirmPassword.trim().isBlank() || confirmPassword.trim().length < 8) {
            liveDataValidation.postValue(ChangePasswordValidation.ConfirmPasswordValidation(R.string.password_must_be_of_min_eight_digits))
            return false
        }

        if (oldPassword.trim().equals(newPassword.trim(),true)) {
            liveDataValidation.postValue(ChangePasswordValidation.NewPasswordValidation(R.string.old_and_new_password_cannot_be_same))
            return false
        }

        if (!newPassword.trim().equals(confirmPassword.trim(),true)) {
            liveDataValidation.postValue(ChangePasswordValidation.ConfirmPasswordValidation(R.string.passwords_do_not_match))
            return false
        }

        return true
    }

    fun onSubmitChangePassword() {
        liveDataVibrator.postValue("")
        if (validateInputs()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = ChangePasswordRepository().changePassword(reqChangePassword())
                    withContext(Dispatchers.Main) {
                        try {
                            when {
                                response.isSuccessful && response.body() != null -> {
                                    Log.i("log", "Change Password Response : ${Gson().toJson(response.body())}")
                                    onChangePasswordResponse(response.body())
                                }
                                response.errorBody() != null -> {
                                    Log.e("log", "Change Password Response: $response")
                                    liveDataResponseStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))
                                }
                                else -> {
                                    Log.e("log", "Registration Response: $response")
                                    liveDataResponseStatus.postValue(
                                        ResponseStatus.TechnicalError(
                                            R.string.some_technical_issue
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            liveDataResponseStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    liveDataResponseStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                } finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onChangePasswordResponse(changePasswordResponseData: ChangePasswordResponseData?) {
        changePasswordResponseData?.let {
            if (changePasswordResponseData.errorCode == 0)
                liveDataResponseStatus.postValue(ResponseStatus.Success(changePasswordResponseData))
            else
                liveDataResponseStatus.postValue(
                    ResponseStatus.Error(changePasswordResponseData.respMsg ?: "", changePasswordResponseData.errorCode)
                )
        }
    }

    private val reqChangePassword: () -> ChangePasswordRequestData = {
        ChangePasswordRequestData(oldPassword = oldPassword, newPassword = newPassword)
    }
}