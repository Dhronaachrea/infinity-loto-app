package com.skilrock.infinitylotoapp.utility

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import com.skilrock.infinitylotoapp.R

object Loader {

    private var dialog: ProgressDialog? = null

    fun showLoader(context: Context?) {
        dismiss()
        //if (dialog == null)
        dialog = ProgressDialog(context)

        dialog?.let { pDialog ->
            val window = pDialog.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            pDialog.isIndeterminate = true
            pDialog.setCancelable(false)
            pDialog.show()
            pDialog.setContentView(R.layout.loader_layout)
            countDownTimer.start()
        }
    }

    fun dismiss() {
        dialog?.let {
            if (it.isShowing) {
                it.dismiss()
                it.cancel()
            }
        }
    }

    private val countDownTimer: CountDownTimer = object : CountDownTimer(1000 * 59, 3200) {
        override fun onTick(millisUntilFinished: Long) {
            //Called on every second
        }

        override fun onFinish() {
            dismiss()
        }
    }



}