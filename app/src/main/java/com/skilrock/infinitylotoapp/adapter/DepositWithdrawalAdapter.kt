package com.skilrock.infinitylotoapp.adapter

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.WithdrawalRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositUiResponseData
import com.skilrock.infinitylotoapp.dialogs.AddNewAccountDialog
import com.skilrock.infinitylotoapp.dialogs.CurrencySelectionDialog
import com.skilrock.infinitylotoapp.dialogs.DepositConfirmationBottomSheetDialog
import com.skilrock.infinitylotoapp.dialogs.SelectionDialog
import com.skilrock.infinitylotoapp.utility.*


class DepositWithdrawalAdapter(
    private val context: Context, private val activity: Activity,
    private val list: ArrayList<DepositUiResponseData.PayTypeMap>,
    private val buttonName: String,
    private val type: String,
    private val callBackOnAccountAdded: () -> Unit,
    private val onDepositClick: (String) -> Unit,
    private val onWithdrawClick: (WithdrawalRequestData) -> Unit
) :
    RecyclerView.Adapter<DepositWithdrawalAdapter.DepositViewHolder>() {

    private var lastClickTime: Long = 0
    private var clickGap = 600

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_deposit, parent, false)

        return DepositViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepositViewHolder, position: Int) {
        val data: DepositUiResponseData.PayTypeMap = list[position]

        Log.i("log", "PayTypeMap: $data")
        holder.btnDeposit.text = buttonName
        holder.etAmount.setText(data.editTextDefaultValue)
        holder.ivInfo.setImageResource(R.drawable.icon_info_dark)

        setPaymentTypeIcon(holder, data)

        handleAddNewAccountButton(holder, data)

        holder.tvAccountName.text = data.payTypeDispCode
        holder.tvMinMax.text = ("Min: ${data.minValue}\nMax: ${data.maxValue}")
        holder.tvCost.text = "0"

        holder.llAccountNumber.setOnClickListener {
            data.accountDetail?.let { listAccount ->
                onAccountClick(holder, listAccount)
            }
        }

        holder.llCurrency.setOnClickListener {
            data.currencyMap?.let { listCurrencyData ->
                onCurrencyClick(holder, listCurrencyData)
            }
        }

        data.accountDetail?.let { listAccount ->
            setAccountNumberToUi(holder, listAccount)
        } ?: run {
            holder.llAccountNumber.visibility = View.GONE
        }

        data.currencyMap?.let { listCurrency ->
            setCurrencyToUi(holder, listCurrency)
        } ?: run {
            holder.llCurrency.visibility = View.GONE
        }

        holder.btnAddNewAccount.setOnClickListener {
            onAddNewAccountClick(holder, data)
        }

        holder.btnDeposit.setOnClickListener {
            try {
                if (validate(holder)) {
                    val header      = getHeaderForConfirmationDialog()
                    val message     = getMessageForConfirmationDialog(holder, data)
                    val buttonText  = getButtonTextForConfirmationDialog()

                    DepositConfirmationBottomSheetDialog(header, message, buttonText) {
                        if (data.showAddNewAccBtn)
                            submitWhenAddAccountIsVisible(holder, data)
                        else
                            submitWhenAddAccountIsNotVisible(holder, data)
                    }.apply {
                        show((holder.llContainer.context as AppCompatActivity).supportFragmentManager, DepositConfirmationBottomSheetDialog.TAG)
                    }
                }
            } catch (e: Exception) {
                activity.redToast(context.getString(R.string.some_technical_issue))
            }
        }
    }

    private fun getHeaderForConfirmationDialog() : String {
        return if (type == "WITHDRAWAL") "Withdrawal Confirmation" else "Deposit Confirmation"
    }

    private fun getButtonTextForConfirmationDialog() : String {
        return if (type == "WITHDRAWAL") "Withdraw" else "Deposit"
    }

    private fun getMessageForConfirmationDialog(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) : String {
        return if (type == "WITHDRAWAL")
            "Are you sure to withdraw <b>${holder.tvCurrency.text.toString().trim()} ${holder.etAmount.text.toString().trim()}</b> using <b>${data.subTypeValue}</b> from your wallet?"
        else
            "Are you sure to deposit <b>${holder.tvCurrency.text.toString().trim()} ${holder.etAmount.text.toString().trim()}</b> using <b>${data.subTypeValue}</b> to your wallet?"
    }

    private fun validate(holder: DepositViewHolder) : Boolean {
        if (holder.etAmount.text.toString().trim().isBlank()) {
            holder.etAmount.error = activity.getString(R.string.enter_amount)
            holder.etAmount.requestFocus()
            return false
        }
        else if (holder.etAmount.text.toString().trim().toInt() == 0) {
            holder.etAmount.error = activity.getString(R.string.enter_valid_amount)
            holder.etAmount.requestFocus()
            return false
        }
        return true
    }

    private fun submitWhenAddAccountIsVisible(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) {
        if (holder.tvAccountNumber.text.toString().trim().isBlank()) {
            activity.redToast(context.getString(R.string.plz_add_select_acc_num_first))
        } else {
            holder.btnDeposit.hideKeyboard()
            if (type == "WITHDRAWAL") {
                val requestData = WithdrawalRequestData(amount=holder.etAmount.text.toString().trim(),
                    currencyCode=holder.tvCurrency.text.toString().trim(),
                    paymentAccId=holder.tvAccountNumber.tag.toString().trim().toInt(),
                    paymentTypeCode=data.payTypeCode,
                    paymentTypeId=data.payTypeId,
                    subTypeId=data.subTypeCode.trim().toInt())

                onWithdrawClick(requestData)
            } else {
                val postData = getFormData(true, data,
                    holder.etAmount.text.toString().trim(),
                    holder.tvCurrency.text.toString().trim(),
                    holder.tvAccountNumber.tag.toString().trim())

                onDepositClick(postData)
            }
        }
    }

    private fun submitWhenAddAccountIsNotVisible(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) {
        if (type == "WITHDRAWAL") {
            val requestData = WithdrawalRequestData(amount=holder.etAmount.text.toString().trim(),
                currencyCode=holder.tvCurrency.text.toString().trim(),
                paymentAccId=null,
                paymentTypeCode=data.payTypeCode,
                paymentTypeId=data.payTypeId,
                subTypeId=data.subTypeCode.trim().toInt())

            onWithdrawClick(requestData)
        } else {
            val postData = getFormData(false, data,
                holder.etAmount.text.toString().trim(),
                holder.tvCurrency.text.toString().trim(), "")

            onDepositClick(postData)
        }
    }

    private fun setPaymentTypeIcon(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) {
        when (data.subTypeCode) {
            "1" -> holder.ivIcon.setBackgroundResource(R.drawable.icon_mpesa)
            "2" -> holder.ivIcon.setBackgroundResource(R.drawable.icon_skrill)
            "3" -> holder.ivIcon.setBackgroundResource(R.drawable.icon_ola)
        }
    }

    private fun handleAddNewAccountButton(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) {
        if (data.showAddNewAccBtn)
            showAddNewAccountButton(holder, data)
        else
            hideAddNewAccountButton(holder)
    }

    private fun showAddNewAccountButton(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) {
        if (data.kycType.equals("OTP", true) || data.kycType.equals("MANUAL", true) || data.kycType.equals("NONE", true)) {
            holder.btnAddNewAccount.visibility = View.VISIBLE
        } else {
            holder.btnAddNewAccount.visibility = View.GONE
            holder.btnDeposit.layoutParams =
                LinearLayoutCompat.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180f, context.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, context.resources.displayMetrics).toInt())
        }
    }

    private fun hideAddNewAccountButton(holder: DepositViewHolder) {
        holder.btnAddNewAccount.visibility = View.GONE
        holder.btnDeposit.layoutParams =
            LinearLayoutCompat.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180f, context.resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, context.resources.displayMetrics).toInt())
    }

    private fun onAccountClick(holder: DepositViewHolder, listAccount: java.util.ArrayList<DepositUiResponseData.PayTypeMap.AccountDetail>) {
        if (listAccount.size > 1 && SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
            SelectionDialog(activity, context.getString(R.string.select_account_number), listAccount) { accountNum: String, paymentAccId: Int ->
                holder.tvAccountNumber.text = accountNum
                holder.tvAccountNumber.tag = paymentAccId
            }.showDialog()
        }
    }

    private fun onCurrencyClick(holder: DepositViewHolder, listCurrencyData: java.util.ArrayList<DepositUiResponseData.PayTypeMap.CurrencyMap>) {
        if (listCurrencyData.size > 1) {
            val listCurrency = ArrayList<String>()

            for (currencyData in listCurrencyData) {
                listCurrency.add(currencyData.value)
            }

            if (SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
                CurrencySelectionDialog(
                    activity,
                    context.getString(R.string.select_currency), listCurrency
                ) { holder.tvCurrency.text = it }.showDialog()
            }
        }
    }

    private fun setAccountNumberToUi(holder: DepositViewHolder, listAccount: java.util.ArrayList<DepositUiResponseData.PayTypeMap.AccountDetail>) {
        when {
            listAccount.size > 1 -> {
                holder.tvAccountNumber.text = listAccount[0].accNum
                holder.tvAccountNumber.tag = listAccount[0].paymentAccId
            }
            listAccount.size == 1 -> {
                holder.tvSelectAccount.text = "A/C"
                holder.tvAccountNumber.text = listAccount[0].accNum
                holder.tvAccountNumber.tag = listAccount[0].paymentAccId
                holder.tvAccountNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            else -> holder.llAccountNumber.visibility = View.GONE
        }
    }

    private fun setCurrencyToUi(holder: DepositViewHolder, listCurrency: java.util.ArrayList<DepositUiResponseData.PayTypeMap.CurrencyMap>) {
        when {
            listCurrency.size > 1 -> {
                holder.tvCurrency.text = listCurrency[0].value
            }
            listCurrency.size == 1 -> {
                holder.tvCurrency.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                holder.tvCurrency.text = listCurrency[0].value
            }
            else -> holder.llCurrency.visibility = View.GONE
        }
    }

    private fun onAddNewAccountClick(holder: DepositViewHolder, data: DepositUiResponseData.PayTypeMap) {
        if (SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
            val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val addNewAccountDialog = AddNewAccountDialog(
                activity, activity.getString(R.string.add_new_account),
                activity.getString(R.string.add), data.payTypeId.toString(),
                data.payTypeCode, data.subTypeCode, data.kycType,
                holder.tvCurrency.text.toString(), ::onAccountAdded
            )
            addNewAccountDialog.show(fm, "AddNewAccountDialog")
        }
    }

    private fun onAccountAdded() {
        callBackOnAccountAdded()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getFormData(isAccountNumberAvailable: Boolean,
                            data: DepositUiResponseData.PayTypeMap, amount: String, currency: String,
                            paymentAccId: String): String {

        if (isAccountNumberAvailable) {
            return "<html>" +
                    "<body onload='document.forms[\"payment_form\"].submit()'>" +
                    "<form action='${DEPOSIT_REQUEST_URL}' method='post' name='payment_form' id='form-submit'>" +
                    "  <input type='hidden' name='paymentTypeId' value='${data.payTypeId}'/>" +
                    "  <input type='hidden' name='txnType' value='${type}'/>" +
                    "  <input type='hidden' name='paymentTypeCode' value='${data.payTypeCode}'/>" +
                    "  <input type='hidden' name='amount' value='${amount}'/>" +
                    "  <input type='hidden' name='paymentAccId' value='${paymentAccId}'/>" +
                    "  <input type='hidden' name='domainName' value='${BuildConfig.DOMAIN_NAME}'/>" +
                    "  <input type='hidden' name='currencyCode' value='${currency}'/>" +
                    "  <input type='hidden' name='subTypeId' value='${data.subTypeCode}'/>" +
                    "  <input type='hidden' name='deviceType' value='${DEVICE_TYPE}'/>" +
                    "  <input type='hidden' name='playerId' value='${PlayerInfo.getPlayerId()}'/>" +
                    "  <input type='hidden' name='playerToken' value='${PlayerInfo.getPlayerToken()}'/>" +
                    "  <input type='hidden' name='merchantCode' value='infiniti'/>" +
                    "</form>" +
                    "</body>" +
                    "</html>"
        }
        else {
            return "<html>" +
                    "<body onload='document.forms[\"payment_form\"].submit()'>" +
                    "<form action='${DEPOSIT_REQUEST_URL}' method='post' name='payment_form' id='form-submit'>" +
                    "  <input type='hidden' name='paymentTypeId' value='${data.payTypeId}'/>" +
                    "  <input type='hidden' name='txnType' value='${type}'/>" +
                    "  <input type='hidden' name='paymentTypeCode' value='${data.payTypeCode}'/>" +
                    "  <input type='hidden' name='amount' value='${amount}'/>" +
                    "  <input type='hidden' name='domainName' value='${BuildConfig.DOMAIN_NAME}'/>" +
                    "  <input type='hidden' name='currencyCode' value='${currency}'/>" +
                    "  <input type='hidden' name='subTypeId' value='${data.subTypeCode}'/>" +
                    "  <input type='hidden' name='deviceType' value='${DEVICE_TYPE}'/>" +
                    "  <input type='hidden' name='playerId' value='${PlayerInfo.getPlayerId()}'/>" +
                    "  <input type='hidden' name='playerToken' value='${PlayerInfo.getPlayerToken()}'/>" +
                    "  <input type='hidden' name='merchantCode' value='infiniti'/>" +
                    "</form>" +
                    "</body>" +
                    "</html>"
        }
    }

    class DepositViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivIcon              = itemView.findViewById(R.id.ivIcon) as AppCompatImageView
        val ivInfo              = itemView.findViewById(R.id.ivInfo) as AppCompatImageView
        val tvAccountName       = itemView.findViewById(R.id.tvAccountName) as MaterialTextView
        val tvMinMax            = itemView.findViewById(R.id.tvMinMax) as MaterialTextView
        val tvCost              = itemView.findViewById(R.id.tvCost) as MaterialTextView
        val tvSelectAccount     = itemView.findViewById(R.id.tvSelectAccount) as MaterialTextView
        val tvAccountNumber     = itemView.findViewById(R.id.tvAccountNumber) as MaterialTextView
        val tvCurrency          = itemView.findViewById(R.id.tvCurrency) as MaterialTextView
        val etAmount            = itemView.findViewById(R.id.etAmount) as EditText
        val llAccountNumber     = itemView.findViewById(R.id.llAccountNumber) as LinearLayoutCompat
        val llContainer         = itemView.findViewById(R.id.container) as LinearLayoutCompat
        val llCurrency          = itemView.findViewById(R.id.llCurrency) as LinearLayoutCompat
        val btnAddNewAccount    = itemView.findViewById(R.id.btnAddNewAccount) as MaterialButton
        val btnDeposit          = itemView.findViewById(R.id.btnDeposit) as MaterialButton
    }

}