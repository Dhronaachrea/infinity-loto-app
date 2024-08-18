package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.ForgotPasswordRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.ForgotPasswordResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class ForgotPasswordRepository {

    suspend fun sendOtp(forgotPasswordRequestData: ForgotPasswordRequestData) : Response<ForgotPasswordResponseData> {
        return RetrofitNetworking.create().callForgotPasswordOtpApi(forgotPasswordRequestData)
    }

    suspend fun resetPassword(forgotPasswordRequestData: ForgotPasswordRequestData) : Response<ForgotPasswordResponseData> {
        return RetrofitNetworking.create().callForgotPasswordResetApi(forgotPasswordRequestData)
    }

}