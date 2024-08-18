package com.skilrock.infinitylotoapp.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.SESSION_EXPIRE_CODE
import com.skilrock.infinitylotoapp.utility.performLogoutCleanUp
import com.skilrock.infinitylotoapp.utility.vibrate
import kotlinx.android.synthetic.main.layout_error_dialog.*

class ErrorDialog(val activity: Activity, private val header: String, private val message: String,
                  private val code: Int = -1, private var intent: Intent? = null,
                  private val operation:()->Unit) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_error_dialog)
        setCancelable(false)

        tvHeader.text = if (header.trim().isEmpty()) context.getString(R.string.service_error) else header
        tvMessage.text = message

        button.setOnClickListener {
            vibrate(activity)
            dismiss()
            if (code == SESSION_EXPIRE_CODE) {
                intent?.let {
                    performLogoutCleanUp(activity, it, true)
                } ?: performLogoutCleanUp(activity)
            }
            else
                operation()
        }
    }

}