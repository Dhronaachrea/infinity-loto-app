package com.skilrock.infinitylotoapp.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxResponseData
import com.skilrock.infinitylotoapp.utility.redToast
import kotlinx.android.synthetic.main.bottom_sheet_inbox.*

class InboxBottomSheetDialog(private val mailData: PlayerInboxResponseData.PlrInbox,
                             val deleteMessage: (ArrayList<Int>) -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "InboxBottomSheetDialog"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvHeader.text   = mailData.subject ?: "NA"
        tvMessage.text  = mailData.contentIdForUse ?: "NA"
        tvDate.text     = (mailData.getDate() + ", " + mailData.getTime())

        btnClose.setOnClickListener {
            dismiss()
        }

        btnDelete.setOnClickListener {
            mailData.inboxId?.let { id ->
                dismiss()
                val list = ArrayList<Int>()
                list.add(id)
                deleteMessage(list)
            } ?: context?.redToast(getString(R.string.some_technical_issue))
        }
    }

}