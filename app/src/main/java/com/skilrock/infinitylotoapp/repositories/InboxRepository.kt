package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.PlayerInboxDeleteRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.PlayerInboxReadRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.PlayerInboxRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxReadResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class InboxRepository {

    suspend fun callInboxApi(playerInboxRequestData: PlayerInboxRequestData) : Response<PlayerInboxResponseData> {
        return RetrofitNetworking.create().callInboxApi(playerInboxRequestData)
    }

    suspend fun callInboxReadApi(playerInboxReadRequestData: PlayerInboxReadRequestData) : Response<PlayerInboxReadResponseData> {
        return RetrofitNetworking.create().callInboxReadApi(playerInboxReadRequestData)
    }

    suspend fun callInboxDeleteApi(playerInboxDeleteRequestData: PlayerInboxDeleteRequestData) : Response<PlayerInboxResponseData> {
        return RetrofitNetworking.create().callInboxDeleteApi(playerInboxDeleteRequestData)
    }

}