package com.skilrock.infinitylotoapp.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.GameEnginesDataClass

class GameListAdapter(private val gameList: ArrayList<GameEnginesDataClass>,
                      private val context: Context, private val height: Int,
                      private val onClick:(GameEnginesDataClass) -> Unit)
    : RecyclerView.Adapter<GameListAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_game_list, parent, false)

        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        if (context.resources.displayMetrics.density > 2.3) {
            holder.llContainer.layoutParams.height = height
        } else {
            holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.low_density_font))

            val params: ViewGroup.MarginLayoutParams = holder.ivIcon.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 3
        }

        holder.tvName.text = gameList[position].gameName
        holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, gameList[position].imagePath))

        holder.llContainer.setOnClickListener { onClick(gameList[position]) }
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivIcon = itemView.findViewById(R.id.ivGameIcon) as AppCompatImageView
        val tvName = itemView.findViewById(R.id.tvGameName) as MaterialTextView
        val llContainer = itemView.findViewById(R.id.container) as LinearLayoutCompat


    }

}