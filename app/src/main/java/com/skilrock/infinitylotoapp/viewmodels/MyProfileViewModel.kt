package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.BalanceRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.LogoutRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LogoutResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.UploadAvatarResponseData
import com.skilrock.infinitylotoapp.repositories.ProfileRepository
import com.skilrock.infinitylotoapp.utility.PARSE_TYPE
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.android.synthetic.main.my_profile_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class MyProfileViewModel : ViewModel() {

    var playerName: String = PlayerInfo.getPlayerUserName()
    var playerId: String = "ID #${PlayerInfo.getPlayerId()}"
    var playerInfo: String =
        PlayerInfo.getPlayerMobileNumber() + "\n" + PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName() + "\n" + PlayerInfo.getPlayerEmailId()
    var playerBalance =
        MutableLiveData(BuildConfig.CURRENCY_SYMBOL + " ${PlayerInfo.getPlayerTotalBalance()}")
    var imageUrl: MutableLiveData<String> =
        MutableLiveData<String>(PlayerInfo.getProfilePicPath())

    val liveDataLoader = MutableLiveData<Boolean>()
    val liveDataVibrator = MutableLiveData<String>()
    val liveDataLogoutStatus = MutableLiveData<ResponseStatus<LogoutResponseData>>()
    val liveDataBalanceStatus = MutableLiveData<ResponseStatus<BalanceResponseData>>()
    val liveDataProfileUpdateStatus = MutableLiveData<ResponseStatus<UploadAvatarResponseData>>()

    fun onBalanceRefreshClick() {
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
                                    ResponseStatus.Error(
                                        response.errorBody().toString(), -1
                                    )
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

    fun onClickLogoutButton() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callLogoutApi(LogoutRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Logout Response: ${Gson().toJson(response.body())}")
                                onLogoutResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Logout Response: $response")
                                liveDataLogoutStatus.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Logout Response: $response")
                                liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Logout Response: $response")
                        liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onLogoutResponse(response: LogoutResponseData?) {
        if (response?.errorCode == 0)
            liveDataLogoutStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataLogoutStatus.postValue(ResponseStatus.Error(response?.respMsg ?: "", -1))
    }

    fun onProfileUpload(path: String) {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ProfileRepository().callUpdateAvatarApi(uploadAvatarRequestParams(), imageMultiPartFromFile(path))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "UploadAvatar Response: ${Gson().toJson(response.body())}")
                                response.body()?.let {
                                    if (it.errorCode == 0) {
                                        it.avatarPath?.let { path ->
                                            imageUrl.value = path
                                            liveDataProfileUpdateStatus.postValue(ResponseStatus.Success(it))
                                        }
                                    }
                                    else {
                                        liveDataProfileUpdateStatus.postValue(ResponseStatus.Error(it.respMsg ?: "Technical Issue", it.errorCode))
                                    }
                                }
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "UploadAvatar Response: $response")
                                liveDataProfileUpdateStatus.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "UploadAvatar Response: $response")
                                liveDataProfileUpdateStatus.postValue(
                                    ResponseStatus.TechnicalError(
                                        R.string.some_technical_issue
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "UploadAvatar Response: $response")
                        liveDataProfileUpdateStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataProfileUpdateStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun imageMultiPartFromFile(path: String): MultipartBody.Part {
        val file = File(path)
        return MultipartBody.Part.createFormData(
            "imageFile",
            file.name,
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        )

    }

    private fun uploadAvatarRequestParams(): HashMap<String, RequestBody> {
        val map = HashMap<String, RequestBody>()
        map["playerToken"] =
            RequestBody.create(MediaType.parse(PARSE_TYPE), PlayerInfo.getPlayerToken())
        map["domainName"] =
            RequestBody.create(MediaType.parse(PARSE_TYPE), BuildConfig.DOMAIN_NAME)
        map["playerId"] =
            RequestBody.create(MediaType.parse(PARSE_TYPE), PlayerInfo.getPlayerId().toString())
        map["imageFileName"] = RequestBody.create(
            MediaType.parse("text/plain"),
            PlayerInfo.getPlayerId().toString() + "_" + PlayerInfo.getPlayerFirstName()
        )
        map["isDefaultAvatar"] = RequestBody.create(MediaType.parse(PARSE_TYPE), "\"Y\"")
        return map
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String) =
            Glide.with(view.context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(view.iv_profilePic)
    }
}

