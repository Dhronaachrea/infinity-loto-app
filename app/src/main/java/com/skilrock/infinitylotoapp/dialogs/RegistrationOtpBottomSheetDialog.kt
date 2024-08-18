package com.skilrock.infinitylotoapp.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.getTrimText
import com.skilrock.infinitylotoapp.utility.showError
import kotlinx.android.synthetic.main.bottom_sheet_otp_registration.*

class RegistrationOtpBottomSheetDialog(private val mobileNumber: String,
                                       val onRegisterClick: (String) -> Unit,
                                       val onResendOtp: () -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "RegistrationOtpBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_otp_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMessage.text  = (getString(R.string.enter_six_digits_otp) + " " + mobileNumber + ".")

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnRegister.setOnClickListener {
            if (validate()) {
                dismiss()
                onRegisterClick(etOtp.getTrimText())
            }
        }

        tvResendOtp.setOnClickListener {
            onResendOtp()
        }
    }

    private fun validate() : Boolean {
        if (etOtp.getTrimText().isBlank()) {
            etOtp.showError(getString(R.string.enter_otp), null,  tilOtp)
            return false
        }
        if (etOtp.getTrimText().length < 6) {
            etOtp.showError(getString(R.string.enter_valid_otp), null,  tilOtp)
            return false
        }
        return true
    }

}