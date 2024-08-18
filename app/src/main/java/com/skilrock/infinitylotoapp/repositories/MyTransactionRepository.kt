package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.MyTransactionRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTransactionResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class MyTransactionRepository {

    suspend fun callApi(myTransactionRequestData: MyTransactionRequestData) : Response<MyTransactionResponseData> {
        return RetrofitNetworking.create().callMyTransactionsApi(myTransactionRequestData)
    }

}