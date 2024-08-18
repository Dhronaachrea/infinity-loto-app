package com.skilrock.infinitylotoapp.data_class.response_data_class


import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.utility.DATE_TIME_TYPE_DESIRED
import java.text.SimpleDateFormat

data class PlayerInboxResponseData(

    @SerializedName("contentPath")
    @Expose
    val contentPath: String?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("plrInboxList")
    @Expose
    val plrInboxList: ArrayList<PlrInbox?>?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("unreadMsgCount")
    @Expose
    val unreadMsgCount: Int?
) {
    data class PlrInbox(

        @SerializedName("contentId")
        @Expose
        val contentId: String?,

        @SerializedName("content_id")
        @Expose
        val contentIdForUse: String?,

        @SerializedName("from")
        @Expose
        val from: String?,

        @SerializedName("inboxId")
        @Expose
        val inboxId: Int?,

        @SerializedName("mailSentDate")
        @Expose
        val mailSentDate: String?,

        @SerializedName("stared")
        @Expose
        val stared: Boolean?,

        @SerializedName("status")
        @Expose
        var status: String?,

        @SerializedName("subject")
        @Expose
        val subject: String?,

        @SerializedName("isSelectedByLongPress")
        @Expose
        var isSelectedByLongPress: Boolean = false
    ) {

        @SuppressLint("SimpleDateFormat")
        fun getDateFormatted() : String {
            return mailSentDate?.let { strDate ->
                val date = SimpleDateFormat(DATE_TIME_TYPE_DESIRED).parse(strDate)
                date?.let {
                    when {
                        DateUtils.isToday(it.time) -> {
                            getTime()
                        }
                        DateUtils.isToday(it.time + DateUtils.DAY_IN_MILLIS) -> {
                            "Yesterday"
                        }
                        else -> {
                            getDate()
                        }
                    }
                } ?: "NA"
            } ?: "NA"
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate() : String {
            return try {
                val parser = SimpleDateFormat(DATE_TIME_TYPE_DESIRED)
                val formatter = SimpleDateFormat("d MMM yyyy")
                mailSentDate?.let { strDate ->
                    val date = parser.parse(strDate)
                    date?.let {
                        formatter.format(it)
                    }
                } ?: "N.A."
            } catch (e: Exception) {
                "NA"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime() : String {
            return try {
                val parser = SimpleDateFormat(DATE_TIME_TYPE_DESIRED)
                val formatter = SimpleDateFormat("hh:mm aa")
                mailSentDate?.let { strDate ->
                    val date = parser.parse(strDate)
                    date?.let {
                        formatter.format(it)
                    }
                } ?: "N.A."
            } catch (e: Exception) {
                "NA"
            }
        }

        fun isMsgRead() : Boolean {
            return status.equals("READ", true)
        }

    }
}