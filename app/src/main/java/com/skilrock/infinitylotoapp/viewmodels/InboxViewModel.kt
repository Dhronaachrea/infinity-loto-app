package com.skilrock.infinitylotoapp.viewmodels

import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.PlayerInboxDeleteRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.PlayerInboxReadRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.PlayerInboxRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxReadResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxResponseData
import com.skilrock.infinitylotoapp.repositories.InboxRepository
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InboxViewModel : BaseViewModel() {

    val activityTitle           = ObservableInt(R.string.inbox)
    val liveDataInbox           = MutableLiveData<ResponseStatus<PlayerInboxResponseData>>()
    val liveDataDeleteMessage   = MutableLiveData<ResponseStatus<PlayerInboxResponseData>>()
    val liveDataReadMessage     = MutableLiveData<ResponseStatus<PlayerInboxReadResponseData>>()

    fun callInboxApi() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = InboxRepository().callInboxApi(PlayerInboxRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Inbox Response: ${Gson().toJson(response.body())}")
                                onInboxResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Inbox Response: $response")
                                liveDataInbox.postValue(
                                    ResponseStatus.Error(
                                        response.errorBody().toString(), -1
                                    )
                                )
                            }
                            else -> {
                                Log.e("log", "Inbox Response: $response")
                                liveDataInbox.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Inbox Response: $response")
                        liveDataInbox.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    } finally {
                        liveDataLoader.postValue(false)
                    }
                }
            } catch (e: Exception) {
                liveDataInbox.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onInboxResponse(response: PlayerInboxResponseData?) {
        response?.let {
            if (it.errorCode == 0) {
                liveDataInbox.postValue(ResponseStatus.Success(response))
            } else
                liveDataInbox.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }

    fun callDeleteMessageApi(inboxIdList: ArrayList<Int>) {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = InboxRepository().callInboxDeleteApi(
                    PlayerInboxDeleteRequestData(inboxIdList = inboxIdList))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Inbox Delete Response: ${Gson().toJson(response.body())}")
                                onDeleteMessageResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Inbox Delete Response: $response")
                                liveDataDeleteMessage.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Inbox Delete Response: $response")
                                liveDataDeleteMessage.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Inbox Delete Response: $response")
                        liveDataDeleteMessage.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    } finally {
                        liveDataLoader.postValue(false)
                    }
                }
            } catch (e: Exception) {
                liveDataDeleteMessage.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onDeleteMessageResponse(response: PlayerInboxResponseData?) {
        response?.let {
            if (it.errorCode == 0) {
                liveDataDeleteMessage.postValue(ResponseStatus.Success(response))
            } else
                liveDataDeleteMessage.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }

    fun callReadMessageApi(inboxId: Int, position: Int) {
        liveDataVibrator.postValue("")
        //liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = InboxRepository().callInboxReadApi(
                    PlayerInboxReadRequestData(inboxId = inboxId))
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Inbox Read Response: ${Gson().toJson(response.body())}")
                                onReadMessageResponse(response.body(), inboxId, position)
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Inbox Read Response: $response")
                                liveDataReadMessage.postValue(
                                    ResponseStatus.Error(response.errorBody().toString(), -1)
                                )
                            }
                            else -> {
                                Log.e("log", "Inbox Read Response: $response")
                                liveDataReadMessage.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Inbox Read Response: $response")
                        liveDataReadMessage.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataReadMessage.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            }
        }
    }

    private fun onReadMessageResponse(response: PlayerInboxReadResponseData?, inboxId: Int, position: Int) {
        response?.let {
            it.inboxId  = inboxId
            it.position = position
            if (it.errorCode == 0) {
                liveDataReadMessage.postValue(ResponseStatus.Success(response))
            } else
                liveDataReadMessage.postValue(ResponseStatus.Error(it.respMsg ?: "", it.errorCode))
        }
    }

}