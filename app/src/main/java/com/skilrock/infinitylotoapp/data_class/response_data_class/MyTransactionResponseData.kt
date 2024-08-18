package com.skilrock.infinitylotoapp.data_class.response_data_class


import android.annotation.SuppressLint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import java.text.SimpleDateFormat
import java.util.*

data class MyTransactionResponseData(

    @SerializedName("errorCode")
    @Expose
    val errorCode: Int,

    @SerializedName("openingBalanceDate")
    @Expose
    val openingBalanceDate: String?,

    @SerializedName("txnList")
    @Expose
    val txnList: ArrayList<Txn?>?,

    @SerializedName("respMsg")
    @Expose
    val respMsg: String?,

    @SerializedName("txnTotalAmount")
    @Expose
    val txnTotalAmount: String?
) {
    data class Txn(

        @SerializedName("balance")
        @Expose
        val balance: Double?,

        @SerializedName("creditAmount")
        @Expose
        val creditAmount: Double?,

        @SerializedName("debitAmount")
        @Expose
        val debitAmount: Double?,

        @SerializedName("currency")
        @Expose
        val currency: String?,

        @SerializedName("currencyId")
        @Expose
        val currencyId: Int?,

        @SerializedName("openingBalance")
        @Expose
        val openingBalance: Double?,

        @SerializedName("particular")
        @Expose
        val particular: String?,

        @SerializedName("subwalletTxn")
        @Expose
        val subwalletTxn: String?,

        @SerializedName("transactionDate")
        @Expose
        val transactionDate: String?,

        @SerializedName("transactionId")
        @Expose
        val transactionId: Int?,

        @SerializedName("txnAmount")
        @Expose
        val txnAmount: Double?,

        @SerializedName("txnType")
        @Expose
        val txnType: String?,

        @SerializedName("withdrawableBalance")
        @Expose
        val withdrawableBalance: Double?,

        @SerializedName("gameGroup")
        @Expose
        val gameGroup: String?
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

        fun getTxnTypeCaption() : String {
            return when (txnType) {
                "PLR_WAGER"                     -> "Wager"
                "PLR_DEPOSIT"                   -> "Deposit"
                "PLR_WITHDRAWAL"                -> "Withdrawal"
                "PLR_WINNING"                   -> "Winning"
                "PLR_WAGER_REFUND"              -> "Wager\nRefund"
                "TRANSFER_IN"                   -> "Cash In"
                "TRANSFER_OUT"                  -> "Cash Out"
                "TRANSFER_OUT_REFUND"           -> "Refund"
                "PLR_DEPOSIT_AGAINST_CANCEL"    -> "Withdrawal\nCancel"
                else -> "NA"
            }
        }

        fun getTxnTypeIcon() : Int? {
            return when (txnType) {
                "PLR_WAGER"                     -> R.drawable.icon_wager
                "PLR_DEPOSIT"                   -> R.drawable.icon_deposit
                "PLR_WITHDRAWAL"                -> R.drawable.icon_withdrawal
                "PLR_WINNING"                   -> R.drawable.icon_winning
                "PLR_WAGER_REFUND"              -> R.drawable.refund
                "TRANSFER_IN"                   -> R.drawable.icon_transfer_in
                "TRANSFER_OUT"                  -> R.drawable.icon_transfer_out
                "TRANSFER_OUT_REFUND"           -> R.drawable.refund
                "PLR_DEPOSIT_AGAINST_CANCEL"    -> R.drawable.icon_withdrawal_cancel
                else -> null
            }
        }

        fun getTxnId() : String {
            return "Txn Id: $transactionId ${getCrDrAmt()}"
        }

        private fun getCrDrAmt() : String {
            if (creditAmount == null && debitAmount != null)
                return "\nDebit Amount: ${PlayerInfo.getCurrencyDisplayCode()} ${String.format("%.2f", debitAmount)}"
            if (debitAmount == null && creditAmount != null)
                return "\nCredit Amount: ${PlayerInfo.getCurrencyDisplayCode()} ${String.format("%.2f", creditAmount)}"

            return ""
        }

        fun getParticularStr() : String {
            return particular ?: ""
        }

        fun getBalanceForDisplay() : String {
            return PlayerInfo.getCurrencyDisplayCode() + " " + String.format("%.2f", balance)
        }
    }
}