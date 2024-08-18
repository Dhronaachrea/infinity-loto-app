package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.FundTransferFromWalletRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.FundTransferFromWalletResponseData
import com.skilrock.infinitylotoapp.repositories.FundTransferFromWalletRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FundTransferAmountViewModel : ViewModel() {

    var amount: String              = ""
    var machineId: String           = ""
    var merchantId: String          = ""
    var ipAddress: String           = ""
    val liveDataVibrator            = MutableLiveData<String>()
    val liveDataLoader              = MutableLiveData<Boolean>()
    val liveDataValidateAmount      = MutableLiveData<Int>()
    val liveDataFundTransferStatus  = MutableLiveData<ResponseStatus<FundTransferFromWalletResponseData>>()

    fun onPayClick() {
        liveDataVibrator.postValue("")
        if (validate()) {
            liveDataLoader.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = FundTransferFromWalletRepository().sendMoney(getRequestData())
                    withContext(Dispatchers.Main) {
                        try {
                            when {
                                response.isSuccessful && response.body() != null -> {
                                    Log.i("log", "FundTransfer Response: ${Gson().toJson(response.body())}")
                                    onFundTransferResponse(response.body())
                                }
                                response.errorBody() != null -> {
                                    Log.e("log", "FundTransfer Response: $response")
                                    liveDataFundTransferStatus.postValue(
                                        ResponseStatus.Error(response.errorBody().toString(), -1)
                                    )
                                }
                                else -> {
                                    Log.e("log", "FundTransfer Response: $response")
                                    liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("log", "FundTransfer Response: $response")
                            liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                        }
                    }
                } catch (e: Exception) {
                    liveDataFundTransferStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
                }
                finally {
                    liveDataLoader.postValue(false)
                }
            }
        }
    }

    private fun onFundTransferResponse(response: FundTransferFromWalletResponseData?) {
        response?.let {
            if (response.errorCode == 0)
                liveDataFundTransferStatus.postValue(ResponseStatus.Success(response))
            else
                liveDataFundTransferStatus.postValue(ResponseStatus.Error(response.errorMsg ?: "", response.errorCode))
        }
    }

    private fun validate() : Boolean {
        if (amount.trim().isBlank()) {
            liveDataValidateAmount.postValue(R.string.enter_amount)
            return false
        }

        if (amount.trim().contains(".")) {
            Log.d("log", "Double Value")
            try {
                val amt = amount.trim().toDouble()
                if (amt == 0.0) {
                    liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                    return false
                }
            } catch (e: Exception) {
                liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                return false
            }
        }
        else {
            Log.d("log", "Integer Value")
            try {
                val amt = amount.trim().toInt()
                if (amt == 0) {
                    liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                    return false
                }
            } catch (e: Exception) {
                liveDataValidateAmount.postValue(R.string.enter_valid_amount)
                return false
            }
        }

        return true
    }

    private fun getRequestData(): FundTransferFromWalletRequestData {
        val amt = (amount.toInt() * 100).toString()
        return FundTransferFromWalletRequestData(amount = amt , machineId = machineId,
            merchantId = merchantId, refTxnNo = "", ipAddress = ipAddress)
    }

}
