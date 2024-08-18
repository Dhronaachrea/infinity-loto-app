package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.MyTicketsRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTicketsResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class MyTicketsRepository {

    suspend fun callApi(myTicketsRequestData: MyTicketsRequestData) : Response<MyTicketsResponseData> {
        return RetrofitNetworking.create().callMyTicketsApi(myTicketsRequestData)
    }

}