package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.AddNewAccountRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.DepositPendingRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.DepositUiRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.AddNewAccountResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositPendingResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import okhttp3.ResponseBody
import retrofit2.Response

class DepositRepository {

    suspend fun callUiApi(depositUiRequestData: DepositUiRequestData) : Response<ResponseBody> {
        return RetrofitNetworking.create().callDepositUiApi(depositUiRequestData=depositUiRequestData)
    }

    suspend fun callDepositPendingApi(depositPendingRequestData: DepositPendingRequestData) : Response<DepositPendingResponseData> {
        return RetrofitNetworking.create().callDepositPendingApi(depositPendingRequestData=depositPendingRequestData)
    }

    suspend fun callAddNewAccountApi(addNewAccountRequestData: AddNewAccountRequestData) : Response<AddNewAccountResponseData> {
        return RetrofitNetworking.create().callAddNewAccountApi(addNewAccountRequestData=addNewAccountRequestData)
    }

}