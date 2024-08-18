package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.FundTransferFromWalletRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.FundTransferFromWalletResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class FundTransferFromWalletRepository {

    suspend fun sendMoney(body: FundTransferFromWalletRequestData) : Response<FundTransferFromWalletResponseData> {
        return RetrofitNetworking.create().callFundTransferFromWalletApi(body)
    }

}