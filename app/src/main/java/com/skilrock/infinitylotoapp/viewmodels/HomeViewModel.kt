package com.skilrock.infinitylotoapp.viewmodels

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.JackpotAndTimerRequestData
import com.skilrock.infinitylotoapp.data_class.request_data_class.LogoutRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.JackpotAndTimerResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LogoutResponseData
import com.skilrock.infinitylotoapp.repositories.HomeRepository
import com.skilrock.infinitylotoapp.utility.GAME_ID_LUCKY_TWELVE
import com.skilrock.infinitylotoapp.utility.GAME_ID_SABANZURI
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeViewModel : BaseViewModel() {

    val liveDataLogoutStatus        = MutableLiveData<ResponseStatus<LogoutResponseData>>()
    val ltAmountTag                 = MutableLiveData<String>()
    val slAmountTag                 = MutableLiveData<String>()
    val slAmount                    = MutableLiveData<String>()
    val ltAmount                    = MutableLiveData<String>()
    val isSlDayVisible              = MutableLiveData(false)
    val slDay                       = MutableLiveData("")
    val slHour                      = MutableLiveData("00")
    val slMinute                    = MutableLiveData("00")
    val slSecond                    = MutableLiveData("00")
    val isLtDayVisible              = MutableLiveData(false)
    val ltDay                       = MutableLiveData("")
    val ltHour                      = MutableLiveData("00")
    val ltMinute                    = MutableLiveData("00")
    val ltSecond                    = MutableLiveData("00")

    private var sabanzuriCountDownTimer: MyCountDownTimer? = null
    private var luckyTwelveCountDownTimer: MyCountDownTimer? = null

    fun performLogout() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = HomeRepository().callLogoutApi(LogoutRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Logout Response: ${Gson().toJson(response.body())}")
                                onLogoutResponse(response.body())
                            }
                            response.errorBody() != null -> {
                                Log.e("log", "Logout Response: $response")
                                liveDataLogoutStatus.postValue(ResponseStatus.Error(response.errorBody().toString(), -1))
                            }
                            else -> {
                                Log.e("log", "Logout Response: $response")
                                liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.some_technical_issue))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Logout Response: $response")
                        liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.something_went_wrong))
                    }
                }
            } catch (e: Exception) {
                liveDataLogoutStatus.postValue(ResponseStatus.TechnicalError(R.string.server_not_responding))
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onLogoutResponse(response: LogoutResponseData?) {
        if (response?.errorCode == 0)
            liveDataLogoutStatus.postValue(ResponseStatus.Success(response))
        else
            liveDataLogoutStatus.postValue(ResponseStatus.Error(response?.respMsg ?: "", -1))
    }

    fun callJackpotAndTimerApi() {
        liveDataVibrator.postValue("")
        liveDataLoader.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = HomeRepository().callJackpotAndTimerApi(JackpotAndTimerRequestData())
                withContext(Dispatchers.Main) {
                    try {
                        when {
                            response.isSuccessful && response.body() != null -> {
                                Log.i("log", "Timer & Jackpot Response: ${Gson().toJson(response.body())}")
                                onJackpotAndTimerResponse(response.body())
                            }
                            else -> {
                                Log.e("log", "Timer & Jackpot Response: $response")
                                setJackpotAndTimerFieldsBlank()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("log", "Timer & Jackpot Response: $response")
                        setJackpotAndTimerFieldsBlank()
                    }
                }
            } catch (e: Exception) {
                setJackpotAndTimerFieldsBlank()
            } finally {
                liveDataLoader.postValue(false)
            }
        }
    }

    private fun onJackpotAndTimerResponse(res: JackpotAndTimerResponseData?) {
        res?.let { response ->
            if (response.errorCode == 0) {
                response.data?.games?.powerBall?.let { sabanzuri ->
                    sabanzuriCountDownTimer?.cancel()

                    sabanzuri.extra?.let { extra ->
                        extra.unitCostJson?.let { list ->
                            setSabanzuriJackpotDetails(list, extra, sabanzuri)
                        } ?: run {
                            slAmount.value      = ""
                            slAmountTag.value   = ""
                        }
                    } ?: run {
                        slAmount.value      = ""
                        slAmountTag.value   = ""
                    }

                    sabanzuriCountDownTimer = MyCountDownTimer(getTimeDifference(sabanzuri.drawDate, response.data.currentTime?.date), 1000, GAME_ID_SABANZURI)
                    sabanzuriCountDownTimer?.start()
                } ?: run {
                    slAmount.value      = ""
                    slAmountTag.value   = ""
                }

                //--------------------

                response.data?.games?.twelveByTwentyFour?.let { luckyTwelve ->
                    luckyTwelveCountDownTimer?.cancel()

                    luckyTwelve.extra?.let { extra ->
                        extra.unitCostJson?.let { list ->
                            setLuckyTwelveJackpotDetails(list, extra, luckyTwelve)
                        } ?: run {
                            ltAmount.value      = ""
                            ltAmountTag.value   = ""
                        }
                    } ?: run {
                        ltAmount.value      = ""
                        ltAmountTag.value   = ""
                    }

                    luckyTwelveCountDownTimer = MyCountDownTimer(getTimeDifference(luckyTwelve.drawDate, response.data.currentTime?.date), 1000, GAME_ID_LUCKY_TWELVE)
                    luckyTwelveCountDownTimer?.start()
                } ?: run {
                    ltAmount.value      = ""
                    ltAmountTag.value   = ""
                }

            }
            else {
                setJackpotAndTimerFieldsBlank()
            }
        } ?: setJackpotAndTimerFieldsBlank()
    }

    private fun setSabanzuriJackpotDetails(list: List<JackpotAndTimerResponseData.Data.Games.POWERBALL.Extra.UnitCostJson?>,
                                           extra: JackpotAndTimerResponseData.Data.Games.POWERBALL.Extra,
                                           sabanzuri: JackpotAndTimerResponseData.Data.Games.POWERBALL) {
        if (list.isNotEmpty()) {
            extra.jackpotAmount?.let { jackpotAmount->
                val costObject = list.find {
                    it?.currency.equals(getCurrencyCode(), true)
                }

                costObject?.price?.let { price ->
                    val format = NumberFormat.getNumberInstance(Locale.US)
                    format.maximumFractionDigits = 2
                    val amount = format.format(jackpotAmount * price)
                    slAmount.value      = (costObject.currency ?: "") + " " + amount
                    slAmountTag.value   = sabanzuri.jackpotTitle ?: ""
                } ?: run {
                    slAmount.value      = ""
                    slAmountTag.value   = ""
                }
            } ?: run {
                slAmount.value      = ""
                slAmountTag.value   = ""
            }
        } else {
            slAmount.value      = ""
            slAmountTag.value   = ""
        }
    }

    private fun setLuckyTwelveJackpotDetails(list: List<JackpotAndTimerResponseData.Data.Games.TWELVEBYTWENTYFOUR.Extra.UnitCostJson?>,
                                             extra: JackpotAndTimerResponseData.Data.Games.TWELVEBYTWENTYFOUR.Extra,
                                             luckyTwelve: JackpotAndTimerResponseData.Data.Games.TWELVEBYTWENTYFOUR) {
        if (list.isNotEmpty()) {
            extra.jackpotAmount?.let { jackpotAmount ->

                val costObject = list.find {
                    it?.currency.equals(getCurrencyCode(), true)
                }

                costObject?.price?.let { price ->
                    val format = NumberFormat.getNumberInstance(Locale.US)
                    format.maximumFractionDigits = 2
                    val amount = format.format(jackpotAmount * price)
                    ltAmount.value      = (costObject.currency ?: "") + " " + amount
                    ltAmountTag.value   = luckyTwelve.jackpotTitle ?: ""
                } ?: run {
                    ltAmount.value      = ""
                    ltAmountTag.value   = ""
                }
            } ?: run {
                ltAmount.value      = ""
                ltAmountTag.value   = ""
            }
        } else {
            ltAmount.value      = ""
            ltAmountTag.value   = ""
        }
    }

    private fun setJackpotAndTimerFieldsBlank() {
        sabanzuriCountDownTimer?.cancel()
        luckyTwelveCountDownTimer?.cancel()
        slAmount.value      = ""
        ltAmount.value      = ""
        slAmountTag.value   = ""
        ltAmountTag.value   = ""
        slHour.value        = "00"
        slMinute.value      = "00"
        slSecond.value      = "00"
        ltHour.value        = "00"
        ltMinute.value      = "00"
        ltSecond.value      = "00"
    }

    private fun getTimeDifference(futureDateStr: String?, currentDateStr: String?): Long {
        var diff: Long = 0
        futureDateStr?.let { futureDateNonNull ->
            currentDateStr?.let { currentDateNonNull ->
                @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                try {
                    val futureDate: Date? = dateFormat.parse(futureDateNonNull)
                    val currentDate: Date? = dateFormat.parse(currentDateNonNull)
                    futureDate?.let { fDate ->
                        currentDate?.let { cDate ->
                            diff = fDate.time - cDate.time
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("log", e.message ?: "ERROR")
                }
            }
        }
        Log.d("log", "Difference: $diff")
        return diff
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long, private val gameId: String) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            val totalSeconds: Int   = (millisUntilFinished / 1000).toInt()
            val days: Int           = totalSeconds / (60 * 60 * 24)
            val hours: Int          = (totalSeconds % (60 * 60 * 24)) / 3600
            val totalMinutes: Int   = (totalSeconds % (60 * 60 * 24)) % 3600
            val minutes: Int        = totalMinutes / 60
            val seconds: Int        = totalMinutes % 60

            if (gameId == GAME_ID_SABANZURI) {
                setTimerForSabanzuri(days, hours, minutes, seconds)
            } else if (gameId == GAME_ID_LUCKY_TWELVE) {
                setTimerForLuckyTwelve(days, hours, minutes, seconds)
            }
        }

        override fun onFinish() {
            liveDataVibrator.postValue("")
            callJackpotAndTimerApi()
        }
    }

    private fun setTimerForSabanzuri(days: Int, hours: Int, minutes: Int, seconds: Int) {
        slDay.value             = if (days < 10) "0$days" else days.toString()
        slHour.value            = if (hours < 10) "0$hours" else hours.toString()
        slMinute.value          = if (minutes < 10) "0$minutes" else minutes.toString()
        slSecond.value          = if (seconds < 10) "0$seconds" else seconds.toString()
        isSlDayVisible.value    = days > 0
    }

    private fun setTimerForLuckyTwelve(days: Int, hours: Int, minutes: Int, seconds: Int) {
        ltDay.value     = if (days < 10) "0$days" else days.toString()
        ltHour.value    = if (hours < 10) "0$hours" else hours.toString()
        ltMinute.value  = if (minutes < 10) "0$minutes" else minutes.toString()
        ltSecond.value  = if (seconds < 10) "0$seconds" else seconds.toString()
        isLtDayVisible.value = days > 0
    }

    private fun getCurrencyCode() : String = PlayerInfo.getPlayerInfo()?.playerLoginInfo?.walletBean?.currency ?: BuildConfig.CURRENCY_DISPLAY_CODE

}