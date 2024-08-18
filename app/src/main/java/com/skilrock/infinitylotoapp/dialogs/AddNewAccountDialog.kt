package com.skilrock.infinitylotoapp.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.request_data_class.AddNewAccountRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.AddNewAccountResponseData
import com.skilrock.infinitylotoapp.databinding.DialogAddNewAccountBinding
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.AddNewAccountViewModel


class AddNewAccountDialog(
    val activity: Activity, private val header: String,
    private val okButtonText: String, private val payTypeId: String,
    private val payTypeCode: String, private val subTypeCode: String,
    private val kycType: String, private val currency: String,
    private val onAccountAdded: () -> Unit
) : DialogFragment() {

    private lateinit var btnCancel: MaterialButton
    private lateinit var btnOk: MaterialButton
    private lateinit var tvHeader: MaterialTextView
    private lateinit var tilAccountHolderName: TextInputLayout
    private lateinit var etAccountHolderName: TextInputEditText
    private lateinit var tilAccountNumber: TextInputLayout
    private lateinit var etAccountNumber: TextInputEditText
    private lateinit var tilOtp: TextInputLayout
    private lateinit var etOtp: TextInputEditText
    private lateinit var viewModel: AddNewAccountViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val binding: DialogAddNewAccountBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_add_new_account, null, false
        )

        viewModel = ViewModelProvider(this).get(AddNewAccountViewModel::class.java)
        binding.viewModel = viewModel

        btnCancel               = binding.root.findViewById(R.id.btnCancel)
        btnOk                   = binding.root.findViewById(R.id.btnOk)
        tvHeader                = binding.root.findViewById(R.id.tvHeader)
        tilAccountHolderName    = binding.root.findViewById(R.id.tilAccountHolderName)
        etAccountHolderName     = binding.root.findViewById(R.id.etAccountHolderName)
        tilAccountNumber        = binding.root.findViewById(R.id.tilAccountNumber)
        etAccountNumber         = binding.root.findViewById(R.id.etAccountNumber)
        tilOtp                  = binding.root.findViewById(R.id.tilOtp)
        etOtp                   = binding.root.findViewById(R.id.etOtp)

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataAddNewAccResponse.observe(this, observerAddNewAcc)
        viewModel.liveDataOtpResponse.observe(this, observerOtp)

        dialog.setContentView(binding.root)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onStart() {
        super.onStart()

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 80)
        dialog?.window?.setBackgroundDrawable(inset)

        dialog?.window?.setLayout(width, height)
    }

    private val observerLoader = Observer<Boolean> {
        btnOk.hideKeyboard()
        if (it) {
            context?.let { cxt ->
                Loader.showLoader(cxt)
            }
        } else Loader.dismiss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvHeader.text   = header
        btnOk.text      = okButtonText
        setClickListeners()
    }

    private fun setClickListeners() {
        btnOk.setOnClickListener {
            btnOk.hideKeyboard()
            when {
                etAccountHolderName.getTrimText().isBlank() -> {
                    context?.let { cxt ->
                        etAccountHolderName.showError(
                            cxt.getString(R.string.enter_account_holder_name),
                            null,
                            tilAccountHolderName
                        )
                    }
                }
                etAccountNumber.getTrimText().isBlank() -> {
                    context?.let { cxt ->
                        etAccountNumber.showError(
                            cxt.getString(R.string.enter_account_number),
                            null,
                            tilAccountNumber
                        )
                    }
                }
                else -> {
                    if (btnOk.tag == "FIRST_CALL") {
                        val requestData = AddNewAccountRequestData(
                            accHolderName = etAccountHolderName.getTrimText(),
                            accNum = etAccountNumber.getTrimText(), paymentTypeCode = payTypeCode,
                            paymentTypeId = payTypeId, subTypeId = subTypeCode, currencyCode = currency
                        )
                        viewModel.callAddNewAccountApi(requestData)
                    }
                    else if (btnOk.tag == "SECOND_CALL") {
                        if (etOtp.getTrimText().isBlank()) {
                            context?.let { cxt ->
                                etOtp.showError(cxt.getString(R.string.enter_otp),
                                    null, tilOtp)
                            }
                        } else {
                            val requestData = AddNewAccountRequestData(
                                accHolderName = etAccountHolderName.getTrimText(),
                                accNum = etAccountNumber.getTrimText(), paymentTypeCode = payTypeCode,
                                paymentTypeId = payTypeId, subTypeId = subTypeCode,
                                currencyCode = currency, verifyOtp = etOtp.getTrimText()
                            )
                            viewModel.callOtpApi(requestData)
                        }
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
            btnCancel.hideKeyboard()
        }
    }

    private val observerAddNewAcc = Observer<ResponseStatus<AddNewAccountResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                if (kycType.equals("OTP", true)) {
                    activity.greenToast(it.response.respMsg ?: getString(R.string.otp_sent))
                    tilOtp.visibility = View.VISIBLE
                    etOtp.requestFocus()
                    disableEditText(etAccountHolderName)
                    disableEditText(etAccountNumber)
                    btnOk.text = getString(R.string.submit)
                    btnOk.tag = "SECOND_CALL"
                } else {
                    dismiss()
                    val successMsg = it.response.respMsg ?: getString(R.string.acc_added_success)
                    activity.greenToast(successMsg)
                    onAccountAdded()
                }
            }

            is ResponseStatus.Error -> {
                activity.redToast(it.errorMessage.getMsg(activity))
            }

            is ResponseStatus.TechnicalError -> {
                activity.redToast(getString(it.errorMessageCode))
            }
        }
    }

    private val observerOtp = Observer<ResponseStatus<AddNewAccountResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                dismiss()
                val successMsg = it.response.respMsg ?: getString(R.string.acc_added_success)
                activity.greenToast(successMsg)
                onAccountAdded()
            }

            is ResponseStatus.Error -> {
                activity.redToast(it.errorMessage.getMsg(activity))
            }

            is ResponseStatus.TechnicalError -> {
                activity.redToast(getString(it.errorMessageCode))
            }
        }
    }

    private fun disableEditText(editText: TextInputEditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        editText.setBackgroundColor(Color.TRANSPARENT)
    }

}