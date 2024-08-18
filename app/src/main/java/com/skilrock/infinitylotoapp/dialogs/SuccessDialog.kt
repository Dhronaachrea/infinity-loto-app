package com.skilrock.infinitylotoapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import androidx.core.text.HtmlCompat
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.vibrate
import kotlinx.android.synthetic.main.layout_success_dialog.*

class SuccessDialog(context: Context, private val header: String, private val message: String,
                    private val isDecoratedMessage: Boolean = false,
                    private val operation:()->Unit) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_success_dialog)
        setCancelable(false)

        tvHeader.text = if (TextUtils.isEmpty(header)) context.getString(R.string.success) else header
        if (isDecoratedMessage)
            tvMessage.text = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
        else
            tvMessage.text = message

        button.setOnClickListener {
            vibrate(context)
            dismiss()
            operation()
        }
    }

}