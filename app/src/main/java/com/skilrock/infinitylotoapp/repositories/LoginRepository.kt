package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.LoginRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class LoginRepository {

    suspend fun performLogin(loginRequestData: LoginRequestData) : Response<LoginResponseData> {
        return RetrofitNetworking.create().callLoginApi(loginRequestData)
    }

}