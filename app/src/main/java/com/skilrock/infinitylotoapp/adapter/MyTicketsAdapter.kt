package com.skilrock.infinitylotoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTicketsResponseData.Ticket
import com.skilrock.infinitylotoapp.databinding.TicketsRowReportBinding
import com.skilrock.infinitylotoapp.utility.SharedPrefUtils
import com.skilrock.infinitylotoapp.utility.THEME_DARK


class MyTicketsAdapter(private val context: Context, private val onClickTicket:(String)->Unit):
    RecyclerView.Adapter<MyTicketsAdapter.MyTicketsViewHolder>() {

    private var ticketList: ArrayList<Ticket?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTicketsViewHolder {
        val binding: TicketsRowReportBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tickets_row_report, parent, false
        )
        return MyTicketsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyTicketsViewHolder, position: Int) {
        holder.ticketsRowReportBinding.ticket = ticketList?.get(position)
        if (SharedPrefUtils.getAppTheme(context) == THEME_DARK) {
            if (holder.ticketsRowReportBinding.ticket?.gameId == "1" || holder.ticketsRowReportBinding.ticket?.gameId == "2")
                holder.ticketsRowReportBinding.tvGameName.setTextColor(ContextCompat.getColor(context, R.color.lilac))
            else
                holder.ticketsRowReportBinding.tvGameName.setTextColor(ContextCompat.getColor(context, R.color.selective_yellow))
        }
        else {
            if (holder.ticketsRowReportBinding.ticket?.gameId == "1" || holder.ticketsRowReportBinding.ticket?.gameId == "2")
                holder.ticketsRowReportBinding.tvGameName.setTextColor(ContextCompat.getColor(context, R.color.tangaroa))
            else
                holder.ticketsRowReportBinding.tvGameName.setTextColor(ContextCompat.getColor(context, R.color.colorText))
        }

        holder.ticketsRowReportBinding.llTicket.setOnClickListener {
            holder.ticketsRowReportBinding.ticket?.let { tkt ->
                tkt.gameId?.let { gameIdNonNull ->
                    if (gameIdNonNull == "1" || gameIdNonNull == "2") {
                        tkt.transactionId?.let { txnId ->
                            onClickTicket(txnId)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return ticketList?.size ?: 0
    }

    fun setTxnList(list: ArrayList<Ticket?>?) {
        ticketList = list
        notifyDataSetChanged()
    }

    fun clearData() {
        ticketList?.clear()
        notifyDataSetChanged()
    }

    class MyTicketsViewHolder(val ticketsRowReportBinding: TicketsRowReportBinding) :
        RecyclerView.ViewHolder(ticketsRowReportBinding.root)
}