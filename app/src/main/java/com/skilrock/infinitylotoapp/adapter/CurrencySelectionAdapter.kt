package com.skilrock.infinitylotoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.infinitylotoapp.R

class CurrencySelectionAdapter(
    private val list: ArrayList<String>,
    private val operation: (String) -> Unit
) :
    RecyclerView.Adapter<CurrencySelectionAdapter.ItemSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSelectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_select_item, parent, false)

        return ItemSelectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemSelectionViewHolder, position: Int) {
        val item = list[position]
        holder.tvItem.text = item

        holder.tvItem.setOnClickListener { operation(item) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ItemSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvItem = itemView.findViewById(R.id.tvItem) as MaterialTextView

    }

}