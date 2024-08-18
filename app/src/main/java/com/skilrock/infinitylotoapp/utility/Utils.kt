package com.skilrock.infinitylotoapp.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.ui.activity.LoginActivity
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun Context.redToast(message: String) {
    val toast   = Toast.makeText(this, message, Toast.LENGTH_LONG)
    val view    = toast.view

    view.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        Color.parseColor(
            "#d30e24"
        ), BlendModeCompat.SRC_ATOP
    )

    val text = view.findViewById<TextView>(android.R.id.message)
    text.setTextColor(Color.WHITE)
    toast.show()
}

fun Activity.redToast(message: String) {
    val toast   = Toast.makeText(this, message, Toast.LENGTH_LONG)
    val view    = toast.view

    view.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        Color.parseColor(
            "#d30e24"
        ), BlendModeCompat.SRC_ATOP
    )

    val text = view.findViewById<TextView>(android.R.id.message)
    text.setTextColor(Color.WHITE)
    toast.show()
}

fun Context.greenToast(message: String) {
    val toast   = Toast.makeText(this, message, Toast.LENGTH_LONG)
    val view    = toast.view

    view.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        Color.parseColor(
            "#089148"
        ), BlendModeCompat.SRC_ATOP
    )

    val text = view.findViewById<TextView>(android.R.id.message)
    text.setTextColor(Color.WHITE)
    toast.show()
}

fun TextInputEditText.showError(
    errorMessage: String = "",
    context: Context?,
    textInputLayout: TextInputLayout
) {
    if (context == null)
        this.error = errorMessage
    else
        context.redToast(errorMessage)
    this.requestFocus()
    textInputLayout.startAnimation(shakeError())
}

fun TextInputEditText.getTrimText() : String {
    return this.text.toString().trim()
}

fun shakeError(): TranslateAnimation {
    val shake = TranslateAnimation(0f, 10f, 0f, 0f)
    shake.duration = 500
    shake.interpolator = CycleInterpolator(7f)
    return shake
}

fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else -> {
            vibrator.vibrate(40)
        }
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun md5(s: String): String {
    val technique = "MD5"
    try {
        // Create MD5 Hash
        val digest = MessageDigest
            .getInstance(technique)
        digest.update(s.toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        val hexString = StringBuilder()
        for (aMessageDigest in messageDigest) {
            var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun Dialog.showDialog() {
    show()
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window?.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
}


fun String.getMsg(context: Context): String {
    return when {
        this.trim().isBlank() -> context.getString(R.string.some_technical_issue)
        else -> this
    }
}

fun performLogoutCleanUp(activity: Activity) {
    SharedPrefUtils.clearAppSharedPref(activity)
    activity.finish()
    val intent = Intent(activity, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun performLogoutCleanUp(activity: Activity, intent: Intent) {
    SharedPrefUtils.clearAppSharedPref(activity)
    activity.finish()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun performLogoutCleanUp(activity: Activity, intent: Intent, wasSessionExpired: Boolean) {
    SharedPrefUtils.clearAppSharedPref(activity)
    activity.finish()
    intent.putExtra("wasSessionExpired", wasSessionExpired)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun getPreviousDate(days: Int): String {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DATE, -days)
    val date = cal.time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat(DATE_TYPE_DESIRED, Locale.ENGLISH)
    return df.format(date)
}

fun getCurrentDate(): String {
    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat(DATE_TYPE_DESIRED, Locale.ENGLISH)
    return df.format(date)
}

fun getPreviousDateYMD(days: Int): String {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DATE, -days)
    val date = cal.time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return df.format(date)
}

fun getCurrentDateYMD(): String {
    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return df.format(date)
}

@SuppressLint("SimpleDateFormat")
fun openDobDialog(context: Context, editText: TextInputEditText) {
    vibrate(context)
    Log.d("log", "Eighteen: " + getPreviousDateYMD(6574))
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearEnd: Int
    val mMonthEnd: Int
    val mDayEnd: Int
    val arrDateStart = getCurrentDate().split("-").toTypedArray()
    mYear   = arrDateStart[2].toInt()
    mMonth  = arrDateStart[1].toInt() - 1
    mDay    = arrDateStart[0].toInt()
    val arrDateEnd = getPreviousDateYMD(6574).split("-").toTypedArray()
    mYearEnd    = arrDateEnd[0].toInt()
    mMonthEnd   = arrDateEnd[1].toInt() - 1
    mDayEnd     = arrDateEnd[2].toInt()
    val dialogStartDate = DatePickerDialog(
        context,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"

            try {
                val parser = SimpleDateFormat(DATE_TYPE_DESIRED)
                val formatter = SimpleDateFormat("MMM d, yyyy")
                val parse = parser.parse(date)
                parse?.let {
                        parsedDate -> editText.setText(formatter.format(parsedDate))
                } ?: run {
                    editText.setText(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                editText.setText(date)
            }
        },
        mYear,
        mMonth,
        mDay
    )
    calender[mYearEnd, mMonthEnd, mDayEnd, 0] = 0
    dialogStartDate.datePicker.maxDate = calender.timeInMillis
    dialogStartDate.show()
}

fun openStartDateDialog(context: Context, tvStartDate: TextView, tvEndDate: TextView) {
    vibrate(context)
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearEnd: Int
    val mMonthEnd: Int
    val mDayEnd: Int
    val arrDate =
        tvStartDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYear   = arrDate[2].toInt()
    mMonth  = arrDate[1].toInt() - 1
    mDay    = arrDate[0].toInt()
    val arrDateEnd =
        tvEndDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYearEnd    = arrDateEnd[2].toInt()
    mMonthEnd   = arrDateEnd[1].toInt() - 1
    mDayEnd     = arrDateEnd[0].toInt()
    val dialogStartDate = DatePickerDialog(
        context,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"
            tvStartDate.text = date
        },
        mYear,
        mMonth,
        mDay
    )
    calender[mYearEnd, mMonthEnd, mDayEnd, 0] = 0
    dialogStartDate.datePicker.maxDate = calender.timeInMillis
    dialogStartDate.show()
}

fun openEndDateDialog(context: Context, tvStartDate: TextView, tvEndDate: TextView) {
    vibrate(context)
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearStart: Int
    val mMonthStart: Int
    val mDayStart: Int
    val arrDate =
        tvEndDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYear   = arrDate[2].toInt()
    mMonth  = arrDate[1].toInt() - 1
    mDay    = arrDate[0].toInt()
    val arrDateStart =
        tvStartDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYearStart  = arrDateStart[2].toInt()
    mMonthStart = arrDateStart[1].toInt() - 1
    mDayStart   = arrDateStart[0].toInt()
    val dialogEndDate = DatePickerDialog(
        context,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"
            tvEndDate.text = date
        },
        mYear,
        mMonth,
        mDay
    )
    dialogEndDate.datePicker.maxDate = Date().time
    calender[mYearStart, mMonthStart, mDayStart, 0] = 0
    dialogEndDate.datePicker.minDate = calender.timeInMillis
    dialogEndDate.show()
}

fun getGameList(context: Context) : ArrayList<GameEnginesDataClass> {
    val gameList = ArrayList<GameEnginesDataClass>()

    val dge             = SharedPrefUtils.getGameData(context, SharedPrefUtils.GAME_DGE)
    val ige             = SharedPrefUtils.getGameData(context, SharedPrefUtils.GAME_IGE)
    val sbs             = SharedPrefUtils.getGameData(context, SharedPrefUtils.GAME_SBS)
    val sle             = SharedPrefUtils.getGameData(context, SharedPrefUtils.GAME_SLE)
    val betGames        = SharedPrefUtils.getGameData(context, SharedPrefUtils.GAME_BET_GAMES)
    val virtualGames    = SharedPrefUtils.getGameData(context, SharedPrefUtils.GAME_VIRTUAL_GAMES)

    if (dge.isNotBlank())
        gameList.add(GsonBuilder().create().fromJson(dge, GameEnginesDataClass::class.java))

    if (ige.isNotBlank())
        gameList.add(GsonBuilder().create().fromJson(ige, GameEnginesDataClass::class.java))

    if (sbs.isNotBlank())
        gameList.add(GsonBuilder().create().fromJson(sbs, GameEnginesDataClass::class.java))

    if (sle.isNotBlank())
        gameList.add(GsonBuilder().create().fromJson(sle, GameEnginesDataClass::class.java))

    if (betGames.isNotBlank())
        gameList.add(GsonBuilder().create().fromJson(betGames, GameEnginesDataClass::class.java))

    if (virtualGames.isNotBlank())
        gameList.add(
            GsonBuilder().create().fromJson(virtualGames, GameEnginesDataClass::class.java)
        )

    return gameList
}

fun getScreenResolution(activity: Activity) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    Log.w("log", "Screen Width: $width")
    Log.w("log", "Screen Height: $height")

    displayMetrics.let {  }
}

fun isNetworkConnected(activity: Activity): Boolean {
    val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo=connectivityManager.activeNetworkInfo
    val flag = networkInfo!=null && networkInfo.isConnected
    if (!flag)
        activity.redToast(activity.getString(R.string.check_internet_connection))
    return flag
}

