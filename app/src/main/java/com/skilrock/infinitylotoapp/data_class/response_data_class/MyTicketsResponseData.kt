package com.skilrock.infinitylotoapp.data_class.response_data_class


import android.annotation.SuppressLint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.*
import java.text.SimpleDateFormat
import java.util.*

data class MyTicketsResponseData(

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("ticketList")
    @Expose
    val ticketList: ArrayList<Ticket?>?
) {
    data class Ticket(

        @SerializedName("amount")
        @Expose
        val amount: String?,

        @SerializedName("gameId")
        @Expose
        val gameId: String?,

        @SerializedName("gameName")
        @Expose
        val gameName: String?,

        @SerializedName("gameType")
        @Expose
        val gameType: String?,

        @SerializedName("refTxnNo")
        @Expose
        val refTxnNo: String?,

        @SerializedName("serviceCode")
        @Expose
        val serviceCode: String?,

        @SerializedName("status")
        @Expose
        val status: String?,

        @SerializedName("transactionDate")
        @Expose
        val transactionDate: String?,

        @SerializedName("transactionId")
        @Expose
        val transactionId: String?,

        @SerializedName("walletProvider")
        @Expose
        val walletProvider: String?
    ) {

        @SuppressLint("SimpleDateFormat")
        fun getDate() : String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val formatter = SimpleDateFormat("d MMM")
                formatter.format(parser.parse(transactionDate))
            } catch (e: Exception) {
                "NA"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime() : String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val formatter = SimpleDateFormat("hh:mm aa")
                formatter.format(parser.parse(transactionDate))
            } catch (e: Exception) {
                "NA"
            }
        }

        fun getTicketIcon() : Int? {
            return when (gameId) {
                GAME_CODE_LUCKY_TWELVE      -> R.drawable.my_tickets_lucky_twelve
                GAME_CODE_SABANZURI         -> R.drawable.my_tickets_sabanzuri
                GAME_CODE_MONEY_BEE         -> R.drawable.my_tickets_money_bee
                GAME_CODE_TREASURE_HUNT     -> R.drawable.my_tickets_treasure_hunt
                GAME_CODE_TIC_TAC_TOE       -> R.drawable.my_tickets_tic_tac_toe
                GAME_CODE_BIG_FIVE          -> R.drawable.my_tickets_big_five
                GAME_CODE_FUN_TIME          -> R.drawable.my_tickets_fun_time
                else -> null
            }
        }

        fun getTxnId() : String {
            return "Txn Id: $transactionId \n${getDate()} ${getTime()}"
        }

        fun getTicketAmount() : String {
            amount?.let {
                return try {
                    val formattedAmount = String.format("%.2f", amount.toDouble())
                    PlayerInfo.getCurrencyDisplayCode() + " " + formattedAmount
                } catch (e: Exception) {
                    e.printStackTrace()
                    PlayerInfo.getCurrencyDisplayCode() + " " + amount
                }
            } ?: return "NA"
        }
    }
}