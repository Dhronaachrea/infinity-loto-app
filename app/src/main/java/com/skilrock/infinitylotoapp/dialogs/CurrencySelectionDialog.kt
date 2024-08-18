package com.skilrock.infinitylotoapp.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.CurrencySelectionAdapter
import com.skilrock.infinitylotoapp.utility.vibrate
import kotlinx.android.synthetic.main.dialog_selection.*
import kotlinx.android.synthetic.main.layout_error_dialog.tvHeader

class CurrencySelectionDialog(val activity: Activity, private val header: String,
                              private val list: ArrayList<String>,
                              private val operation:(String)->Unit) : Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_selection)
        setCancelable(true)

        tvHeader.text = header

        rvItems.layoutManager = GridLayoutManager(context, 2)
        rvItems.setHasFixedSize(true)
        val adapter = CurrencySelectionAdapter(list, ::onItemSelect)
        rvItems.adapter = adapter
    }

    private fun onItemSelect(currency: String) {
        vibrate(activity)
        operation(currency)
        dismiss()
    }

}