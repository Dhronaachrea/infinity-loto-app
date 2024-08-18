package com.skilrock.infinitylotoapp.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.infinitylotoapp.R
import kotlinx.android.synthetic.main.bottom_sheet_profile_update.*

class ProfileUpdateBottomSheetDialog(private val data: String,
                                     val onUpdateClick:() -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ProfileUpdateBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_profile_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMessage.text = HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnUpdateBottomSheet.setOnClickListener {
            dismiss()
            onUpdateClick()
        }
    }

}