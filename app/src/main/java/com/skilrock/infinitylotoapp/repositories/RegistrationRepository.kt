package com.skilrock.infinitylotoapp.repositories

import com.skilrock.infinitylotoapp.data_class.request_data_class.RegistrationOtpRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.RegistrationRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.RegistrationOtpResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.RegistrationResponseData
import com.skilrock.infinitylotoapp.network.RetrofitNetworking
import retrofit2.Response

class RegistrationRepository {

    suspend fun registerPlayer(registrationRequestData: RegistrationRequestData) : Response<RegistrationResponseData> {
        return RetrofitNetworking.create().callRegistrationApi(registrationRequestData)
    }

    suspend fun registrationOtp(registrationOtpRequestData: RegistrationOtpRequestData) : Response<RegistrationOtpResponseData> {
        return RetrofitNetworking.create().callRegistrationOtpApi(registrationOtpRequestData)
    }

}