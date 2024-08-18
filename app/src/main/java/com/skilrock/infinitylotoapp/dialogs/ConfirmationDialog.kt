package com.skilrock.infinitylotoapp.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.vibrate
import kotlinx.android.synthetic.main.dialog_confirmation.*
import kotlinx.android.synthetic.main.layout_error_dialog.tvHeader
import kotlinx.android.synthetic.main.layout_error_dialog.tvMessage

class ConfirmationDialog(val activity: Activity, private val header: String, private val message: String,
                         private val operation:()->Unit) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirmation)
        setCancelable(false)

        tvHeader.text = if (header.trim().isEmpty()) context.getString(R.string.service_error) else header
        tvMessage.text = message

        btnOk.setOnClickListener {
            vibrate(activity)
            dismiss()
            operation()
        }

        btnCancel.setOnClickListener {
            vibrate(activity)
            dismiss()
        }
    }

}