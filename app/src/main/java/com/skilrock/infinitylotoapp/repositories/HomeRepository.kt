package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.JackpotAndTimerRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.LogoutRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.JackpotAndTimerResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LogoutResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class HomeRepository {

    suspend fun callLogoutApi(logoutRequestData: LogoutRequestData) : Response<LogoutResponseData> {
        return RetrofitNetworking.create().callLogoutApi(logoutRequestData)
    }

    suspend fun callJackpotAndTimerApi(jackpotAndTimerRequestData: JackpotAndTimerRequestData) : Response<JackpotAndTimerResponseData> {
        return RetrofitNetworking.create().callJackpotAndTimerApi(jackpotAndTimerRequestData)
    }

}