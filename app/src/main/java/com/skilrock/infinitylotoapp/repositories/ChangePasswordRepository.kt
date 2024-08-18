package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.ChangePasswordRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.ChangePasswordResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class ChangePasswordRepository {

    suspend fun changePassword(changePasswordRequestData: ChangePasswordRequestData): Response<ChangePasswordResponseData> {
        return RetrofitNetworking.create().callChangePasswordApi(changePasswordRequestData)
    }
}