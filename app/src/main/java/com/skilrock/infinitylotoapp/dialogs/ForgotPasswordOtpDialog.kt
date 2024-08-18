package com.skilrock.infinitylotoapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.inputmethod.EditorInfo
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.*
import kotlinx.android.synthetic.main.dialog_otp_forgot_password.*


class ForgotPasswordOtpDialog(context: Context, private val otpInfo: String,
                              private val resetPassword:(String, String, String)->Unit) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_otp_forgot_password)
        setCancelable(false)

        tvInfo.text = otpInfo
        btnCancel.setOnClickListener {
            vibrate(context)
            dismiss()
        }

        btnSubmit.setOnClickListener {
            vibrate(context)
            if (validateInputs()) {
                dismiss()
                resetPassword(etOtp.getTrimText(), etNewPassword.getTrimText(), etConfirmPassword.getTrimText())
            }
        }

        etConfirmPassword.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnSubmit.hideKeyboard()
                    btnSubmit.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun validateInputs() : Boolean {
        if (etOtp.text.toString().trim().isEmpty()) {
            etOtp.showError(context.getString(R.string.enter_otp), null,  tilOtp)
            return false
        }

        if (etNewPassword.text.toString().trim().isEmpty() || etNewPassword.text.toString().trim().length < 8) {
            etNewPassword.showError(context.getString(R.string.password_eight_characters), context,  tilNewPassword)
            return false
        }

        if (etConfirmPassword.text.toString().trim().isEmpty() || etConfirmPassword.text.toString().trim().length < 8) {
            etConfirmPassword.showError(context.getString(R.string.password_eight_characters), context,  tilConfirmPassword)
            return false
        }

        if (!etNewPassword.text.toString().trim().equals(etConfirmPassword.text.toString().trim(), ignoreCase = true)) {
            etConfirmPassword.showError(context.getString(R.string.passwords_do_not_match), context,  tilConfirmPassword)
            return false
        }

        return true
    }

}