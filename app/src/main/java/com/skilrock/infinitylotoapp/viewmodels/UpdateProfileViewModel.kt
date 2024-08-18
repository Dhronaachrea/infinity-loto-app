package com.skilrock.infinitylotoapp.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.UpdateProfileRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.UpdateProfileResponseData
import com.skilrock.infinitylotoapp.repositories.ProfileRepository
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


class UpdateProfileViewModel : BaseViewModel() {

    val activityTitle   = ObservableInt(R.string.update_profile)
    var firstName       = MutableLiveData(PlayerInfo.getPlayerFirstName())
    var lastName        = MutableLiveData(PlayerInfo.getPlayerLastName())
    var email           = MutableLiveData(PlayerInfo.getPlayerEmailId())
    var address         = MutableLiveData(PlayerInfo.getPlayerInfo()?.playerLoginInfo?.addressLine1 ?: "")
    var country         = MutableLiveData(PlayerInfo.getPlayerCountry())
    var gender          = ""

    val liveDataUpdateProfile       = MutableLiveData<ResponseStatus<UpdateProfileResponseData>>()
    val liveDataUpdatePlayerInfo    = MutableLiveData<UpdateProfileResponseData.PlayerInfoBean>()

    fun callProfileUpdateApi(strDate: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callProfileUpdateApi(getProfileUpdateRequestData(strDate))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Profile Update Response: ${Gson().toJson(response.body())}")
                                onProfileUpdateResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Profile Update Response: $response")
                                liveDataUpdateProfile.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))
                            }
                            else -> {
                                Log.e("log", "Profile Update Response: $response")
                                liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Profile Update Response: $response")
                        liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    } finally {
                        liveDataLoader.postValue(false)
                    }
                }
            } catch (e: Exception) {
                liveDataUpdateProfile.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onProfileUpdateResponse(response: UpdateProfileResponseData?) {
        response?.let {
            if (it.errorCode == 0) {
                liveDataUpdateProfile.postValue(ResponseStatus.Success(response))
            } else {
                it.playerInfoBean?.let { plrInfoBean ->
                    liveDataUpdatePlayerInfo.postValue(plrInfoBean)
                }
                liveDataUpdateProfile.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
            }
        }
    }

    private fun getProfileUpdateRequestData(strDate: String) : UpdateProfileRequestData {
        return UpdateProfileRequestData(firstName=firstName.value, lastName=lastName.value, emailId=email.value, dob=getDob(strDate, "MMM d, yyyy", "yyyy-MM-dd"), addressLine1=address.value, gender=getGenderForRequestParam())
    }

    private fun getGenderForRequestParam() : String {
        return when (gender) {
            "Male" -> "M"
            "Female" -> "F"
            else -> ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDob(strDate: String, sourceFormat: String, targetFormat: String) : String {
        if (strDate.isBlank())
            return ""
        else {
            try {
                val parser = SimpleDateFormat(sourceFormat)
                val formatter = SimpleDateFormat(targetFormat)
                val parse = parser.parse(strDate)
                parse?.let {
                    parsedDate -> return formatter.format(parsedDate)
                } ?: return strDate
            } catch (e: Exception) {
                return strDate
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateOfBirth() : String {
        PlayerInfo.getPlayerInfo()?.playerLoginInfo?.dob?.let {
            try {
                val parser = SimpleDateFormat("dd/MM/yyyy")
                val formatter = SimpleDateFormat("MMM d, yyyy")
                val parse = parser.parse(it)
                parse?.let {
                        parsedDate -> return formatter.format(parsedDate)
                } ?: return it
            } catch (e: Exception) {
                return it
            }
        } ?: return ""
    }

}

