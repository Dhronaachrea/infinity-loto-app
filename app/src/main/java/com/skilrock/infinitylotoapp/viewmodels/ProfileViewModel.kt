package com.skilrock.infinitylotoapp.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.BalanceRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.EmailCodeRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.EmailVerifyRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.*
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
import java.text.SimpleDateFormat

class ProfileViewModel : BaseViewModel() {

    val activityTitle       = ObservableInt(R.string.profile)
    var playerId: String    = "ID #${PlayerInfo.getPlayerId()}"
    var playerProfileName   = PlayerInfo.getPlayerUserName()
    var mobile              = MutableLiveData(PlayerInfo.getPlayerMobileNumber())
    var email               = MutableLiveData(PlayerInfo.getPlayerEmailId())
    var dob                 = MutableLiveData(getDateOfBirth())
    var gender              = MutableLiveData(PlayerInfo.getPlayerGender())
    var country             = MutableLiveData(PlayerInfo.getPlayerCountry())
    private var address     = MutableLiveData(PlayerInfo.getPlayerAddress())
    private var fullName    = MutableLiveData(PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName())

    var profileImageUrl: MutableLiveData<String> =
        MutableLiveData<String>(PlayerInfo.getProfilePicPath())

    val liveDataLogoutStatus        = MutableLiveData<ResponseStatus<LogoutResponseData>>()
    val liveDataBalanceStatus       = MutableLiveData<ResponseStatus<BalanceResponseData>>()
    val liveDataEmailCodeStatus     = MutableLiveData<ResponseStatus<EmailCodeResponseData>>()
    val liveDataEmailVerifyStatus   = MutableLiveData<ResponseStatus<EmailVerifyResponseData>>()
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

    fun callEmailCodeApi(openDialog: Boolean) {
        Log.d("log", "Open Dialog: $openDialog")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callEmailCodeApi(EmailCodeRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Email Code Response: ${Gson().toJson(response.body())}")
                                onEmailCodeResponse(response.body(), openDialog)
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Email Code Response: $response")
                                liveDataEmailCodeStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))
                            }
                            else -> {
                                Log.e("log", "Email Code Response: $response")
                                liveDataEmailCodeStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Email Code Response: $response")
                        liveDataEmailCodeStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataEmailCodeStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onEmailCodeResponse(response: EmailCodeResponseData?, openDialog: Boolean) {
        response?.let {
            if (it.errorCode == 0) {
                it.openDialog = openDialog
                liveDataEmailCodeStatus.postValue(ResponseStatus.Success(it))
            }
            else
                liveDataEmailCodeStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }

    fun callEmailVerifyApi(code: String) {
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ProfileRepository().callEmailVerifyApi(EmailVerifyRequestData(verificationCode=code))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Email Verify Response: ${Gson().toJson(response.body())}")
                                onEmailVerifyResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Email Verify Response: $response")
                                liveDataEmailVerifyStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))
                            }
                            else -> {
                                Log.e("log", "Email Verify Response: $response")
                                liveDataEmailVerifyStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Email Verify Response: $response")
                        liveDataEmailVerifyStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataEmailVerifyStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
            finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onEmailVerifyResponse(response: EmailVerifyResponseData?) {
        response?.let {
            if (it.errorCode == 0)
                liveDataEmailVerifyStatus.postValue(ResponseStatus.Success(it))
            else
                liveDataEmailVerifyStatus.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
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
                                            profileImageUrl.value = path
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
                liveDataProfileUpdateStatus.postValue(ResponseStatus.TechnicalError(R.string.failed_to_upload_image))
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
            MediaType.parse(PARSE_TYPE),
            PlayerInfo.getPlayerId().toString() + "_" + PlayerInfo.getPlayerFirstName()
        )
        map["isDefaultAvatar"] = RequestBody.create(MediaType.parse(PARSE_TYPE), "\"Y\"")
        return map
    }

    companion object {
        @JvmStatic
        @BindingAdapter("profileImageUrl")
        fun loadImage(view: ImageView, profileImageUrl: String) =
            Glide.with(view.context)
                .load(profileImageUrl)
                .apply(
                    RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(view.iv_profilePic)
    }

    fun refreshPlayerInformation() {
        fullName.value  = PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
        email.value     = PlayerInfo.getPlayerEmailId()
        dob.value       = getDateOfBirth()
        gender.value    = PlayerInfo.getPlayerGender()
        address.value   = PlayerInfo.getPlayerAddress()
        country.value   = PlayerInfo.getPlayerCountry()
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