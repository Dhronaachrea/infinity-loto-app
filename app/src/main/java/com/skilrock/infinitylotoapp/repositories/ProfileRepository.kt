package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.*
import com.skilrock.infinitylotoapp.data_class.response_data_class.*
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ProfileRepository {

    suspend fun callLogoutApi(logoutRequestData: LogoutRequestData) : Response<LogoutResponseData> {
        return RetrofitNetworking.create().callLogoutApi(logoutRequestData)
    }

    suspend fun callBalanceApi(balanceRequestData: BalanceRequestData) : Response<BalanceResponseData> {
        return RetrofitNetworking.create().callUpdateBalanceApi(balanceRequestData)
    }

    suspend fun callUpdateAvatarApi(requestParamsMap: HashMap<String, RequestBody>, multipartFile: MultipartBody.Part) : Response<UploadAvatarResponseData>{
        return RetrofitNetworking.create().callProfilePicApi(requestParamsMap, multipartFile)
    }

    suspend fun callProfileUpdateApi(updateProfileRequestData: UpdateProfileRequestData): Response<UpdateProfileResponseData> {
        return RetrofitNetworking.create().callProfileUpdateApi(updateProfileRequestData)
    }

    suspend fun callEmailCodeApi(emailCodeRequestData: EmailCodeRequestData): Response<EmailCodeResponseData> {
        return RetrofitNetworking.create().callEmailCodeApi(emailCodeRequestData)
    }

    suspend fun callEmailVerifyApi(emailVerifyRequestData: EmailVerifyRequestData): Response<EmailVerifyResponseData> {
        return RetrofitNetworking.create().callEmailVerifyApi(emailVerifyRequestData)
    }

}