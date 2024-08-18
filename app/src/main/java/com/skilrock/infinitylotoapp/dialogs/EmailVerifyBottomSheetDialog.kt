package com.skilrock.infinitylotoapp.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.getTrimText
import com.skilrock.infinitylotoapp.utility.showError
import kotlinx.android.synthetic.main.bottom_sheet_verify_email.*

class EmailVerifyBottomSheetDialog(private val email: String, val onVerifyClick: (String) -> Unit,
                                   val onResendCodeClick: () -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "EmailVerifyBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_verify_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message     = getString(R.string.verification_code_sent_to) + " <b>" + email + "</b>."
        tvMessage.text  = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnVerify.setOnClickListener {
            if (validate()) {
                onVerifyClick(etVerificationCode.getTrimText())
            }
        }

        tvResendCode.setOnClickListener { onResendCodeClick() }
    }

    private fun validate() : Boolean {
        if (etVerificationCode.getTrimText().isBlank()) {
            etVerificationCode.showError(getString(R.string.enter_verification_code), null,  tilVerificationCode)
            return false
        }
        return true
    }

}