package com.skilrock.infinitylotoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTransactionResponseData.Txn
import com.skilrock.infinitylotoapp.databinding.TransactionRowReportBinding


class MyTransactionAdapter : RecyclerView.Adapter<MyTransactionAdapter.MyTransactionViewHolder>() {

    private var txnList: ArrayList<Txn?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTransactionViewHolder {
        val binding: TransactionRowReportBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.transaction_row_report, parent, false)
        return MyTransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyTransactionViewHolder, position: Int) {
        holder.rowTransactionReportBinding.txn = txnList?.get(position)
    }

    override fun getItemCount(): Int {
        return txnList?.size ?: 0
    }

    fun setTxnList(list: ArrayList<Txn?>?) {
        txnList = list
        notifyDataSetChanged()
    }

    fun clearData() {
        txnList?.clear()
        notifyDataSetChanged()
    }

    class MyTransactionViewHolder(val rowTransactionReportBinding: TransactionRowReportBinding) :
        RecyclerView.ViewHolder(rowTransactionReportBinding.root)
}