package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.VersionRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.VersionResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class SplashRepository {

    suspend fun callVersionApi(versionRequestData: VersionRequestData) : Response<VersionResponseData> {
        return RetrofitNetworking.create().callVersionApi(versionRequestData)
    }

}