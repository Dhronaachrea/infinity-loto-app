package com.skilrock.infinitylotoapp.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.infinitylotoapp.R
import kotlinx.android.synthetic.main.bottom_sheet_deposit_confirmation.*

class DepositConfirmationBottomSheetDialog(private val header: String, private val message: String,
                                           private val buttonText: String,
                                           val performOperation: () -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "DepositConfirmationBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_deposit_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvHeader.text   = header
        tvMessage.text  = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
        btnDone.text    = buttonText

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnDone.setOnClickListener {
            dismiss()
            performOperation()
        }

    }

}