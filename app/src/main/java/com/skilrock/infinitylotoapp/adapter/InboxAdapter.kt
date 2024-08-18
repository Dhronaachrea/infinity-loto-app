package com.skilrock.infinitylotoapp.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxResponseData.PlrInbox
import com.skilrock.infinitylotoapp.databinding.RowInboxBinding
import com.skilrock.infinitylotoapp.utility.SharedPrefUtils
import com.skilrock.infinitylotoapp.utility.THEME_DARK
import java.util.*
import kotlin.collections.ArrayList


class InboxAdapter(
    private val context: Context, private val onClickMail: (PlrInbox, Int) -> Unit,
    private val onLongPressMail: (PlrInbox) -> Unit
):
    RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {

    companion object {
        var IS_LONG_PRESS_ACTIVATED = false
    }

    private var listMail: ArrayList<PlrInbox?>? = null
    private var listMailCopy: ArrayList<PlrInbox?>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        val binding: RowInboxBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_inbox, parent, false
        )

        return InboxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        holder.rowInboxBinding.inboxData = listMail?.get(position)
        holder.rowInboxBinding.containerInboxRow.setOnClickListener {
            holder.rowInboxBinding.inboxData?.let {
                if (IS_LONG_PRESS_ACTIVATED) {
                    it.isSelectedByLongPress = !it.isSelectedByLongPress
                    notifyItemChanged(position)
                    onLongPressMail(it)
                } else
                    onClickMail(it, position)
            }
        }

        holder.rowInboxBinding.containerInboxRow.setOnLongClickListener {
            holder.rowInboxBinding.inboxData?.let {
                it.isSelectedByLongPress = !it.isSelectedByLongPress
                notifyItemChanged(position)
                onLongPressMail(it)
            }
            true
        }

        holder.rowInboxBinding.inboxData?.let { data ->
            setInboxUiAndData(holder, data)
        }
    }

    private fun setInboxUiAndData(holder: InboxViewHolder, data: PlrInbox) {
        if (data.isMsgRead()) {
            holder.rowInboxBinding.tvInboxSubject.setTypeface(null, Typeface.NORMAL)
            holder.rowInboxBinding.tvInboxSubject.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        } else {
            holder.rowInboxBinding.tvInboxSubject.setTypeface(null, Typeface.BOLD)
            holder.rowInboxBinding.tvInboxSubject.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        }

        if (SharedPrefUtils.getAppTheme(context) == THEME_DARK) {
            if (holder.rowInboxBinding.inboxData?.isSelectedByLongPress == true) {
                holder.rowInboxBinding.ivCheck.visibility = View.VISIBLE
                holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#1a264b"))
            } else {
                holder.rowInboxBinding.ivCheck.visibility = View.GONE
                holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#010F38"))
            }
        }
        else {
            if (holder.rowInboxBinding.inboxData?.isSelectedByLongPress == true) {
                holder.rowInboxBinding.ivCheck.visibility = View.VISIBLE
                holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#e0e0e0"))
            } else {
                holder.rowInboxBinding.ivCheck.visibility = View.GONE
                holder.rowInboxBinding.containerInboxRow.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

    fun readMessage(inboxId: Int, position: Int) {
        listMail?.get(position)?.let { messageData ->
            if (messageData.inboxId == inboxId) {
                messageData.status = "READ"
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listMail?.size ?: 0
    }

    fun setInboxList(list: ArrayList<PlrInbox?>?) {
        listMail = list
        listMailCopy?.clear()
        list?.let {
            for (mail in it) { listMailCopy?.add(mail) }
        }
        IS_LONG_PRESS_ACTIVATED = false
        notifyDataSetChanged()
    }

    fun resetLongPressRows() {
        IS_LONG_PRESS_ACTIVATED = false
        for (index in 0 until (listMail?.size ?: 0))
            listMail?.get(index)?.isSelectedByLongPress = false

        notifyDataSetChanged()
    }

    fun selectAll(flag: Boolean) {
        for (index in 0 until (listMail?.size ?: 0))
            listMail?.get(index)?.isSelectedByLongPress = flag

        notifyDataSetChanged()
    }

    fun getAllInboxId() : ArrayList<Int> {
        val list = ArrayList<Int>()
        for (index in 0 until (listMail?.size ?: 0)) {
            listMail?.get(index)?.inboxId?.let { id ->
                list.add(id)
            }
        }
        return list
    }

    fun clearData() {
        listMail?.clear()
        notifyDataSetChanged()
    }

    fun searchFilter(text: String) {
        if (text.isBlank()) {
            listMail = listMailCopy
            notifyDataSetChanged()
        } else {
            val filteredList = ArrayList<PlrInbox?>()
            listMailCopy?.let {
                for (mail in it) {
                    if ((mail?.subject?.toLowerCase(Locale.ROOT)
                            ?.contains(text.toLowerCase(Locale.ROOT)) == true) or (mail?.contentIdForUse?.toLowerCase(
                            Locale.ROOT
                        )
                            ?.contains(text.toLowerCase(Locale.ROOT)) == true) ) {
                        filteredList.add(mail)
                    }
                }
                listMail = filteredList
                notifyDataSetChanged()
            }
        }
    }

    fun getInboxId(position: Int): Int? {
        return listMail?.get(position)?.inboxId
    }

    override fun getItemId(position: Int): Long {
        return listMail?.get(position)?.inboxId?.toLong() ?: -1
    }

    class InboxViewHolder(val rowInboxBinding: RowInboxBinding) :
        RecyclerView.ViewHolder(rowInboxBinding.root)
}