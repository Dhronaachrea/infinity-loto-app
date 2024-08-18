package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.*
import com.skilrock.infinitylotoapp.data_class.response_data_class.*
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import okhttp3.ResponseBody
import retrofit2.Response

class WithdrawalRepository {

    suspend fun callUiApi(withdrawalUiRequestData: WithdrawalUiRequestData) : Response<ResponseBody> {
        return RetrofitNetworking.create().callWithdrawalUiApi(withdrawalUiRequestData=withdrawalUiRequestData)
    }

    suspend fun callDepositPendingApi(depositPendingRequestData: DepositPendingRequestData) : Response<DepositPendingResponseData> {
        return RetrofitNetworking.create().callDepositPendingApi(depositPendingRequestData=depositPendingRequestData)
    }

    suspend fun callWithdrawRequestApi(withdrawalRequestData: WithdrawalRequestData) : Response<WithdrawResponseData> {
        return RetrofitNetworking.create().callWithdrawRequestApi(withdrawalRequestData=withdrawalRequestData)
    }

}