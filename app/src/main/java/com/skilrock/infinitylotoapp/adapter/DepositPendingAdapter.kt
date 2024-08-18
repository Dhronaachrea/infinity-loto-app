package com.skilrock.infinitylotoapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositPendingResponseData
import java.text.SimpleDateFormat

class DepositPendingAdapter(private val list: ArrayList<DepositPendingResponseData.Txn?>) :
    RecyclerView.Adapter<DepositPendingAdapter.DepositPendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositPendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_deposit_pending, parent, false)

        return DepositPendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepositPendingViewHolder, position: Int) {
        val pendingItem = list[position]

        pendingItem?.let { item ->
            holder.tvCount.text     = (position + 1).toString()
            holder.tvTxnId.text     = item.userTxnId.toString()
            holder.tvAmount.text    = item.amount.toString()

            item.txnDate?.let { strDate ->
                holder.tvDate.text  = (getDate(strDate) + " | " + getTime(strDate))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(strDate: String) : String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val formatter = SimpleDateFormat("d MMM")
            parser.parse(strDate)?.let {
                formatter.format(it)
            } ?: "NA"
        } catch (e: Exception) {
            "NA"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(strDate: String) : String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            val formatter = SimpleDateFormat("hh:mm aa")
            parser.parse(strDate)?.let {
                formatter.format(it)
            } ?: "NA"
        } catch (e: Exception) {
            "NA"
        }
    }

    class DepositPendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvCount     = itemView.findViewById(R.id.tvCount) as MaterialTextView
        val tvTxnId     = itemView.findViewById(R.id.tvTxnId) as MaterialTextView
        val tvDate      = itemView.findViewById(R.id.tvDate) as MaterialTextView
        val tvAmount    = itemView.findViewById(R.id.tvAmount) as MaterialTextView

    }

}