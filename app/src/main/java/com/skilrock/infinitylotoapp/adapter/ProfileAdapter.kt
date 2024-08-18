package com.skilrock.infinitylotoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.utility.PlayerInfo

class ProfileAdapter(val context: Context, private val mapInfo: LinkedHashMap<String, String>, val onVerifyClick: ()-> Unit)
    : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    private val listItems = ArrayList<String>()

    init {
        val keys: MutableSet<String> = mapInfo.keys

        val iterator = keys.iterator()
        while (iterator.hasNext())
            listItems.add(iterator.next())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvItem.text = mapInfo[item]

        when (item) {
            "name" -> {
                holder.llProfileRow.setBackgroundResource(0)
                holder.ivIcon.setImageResource(R.drawable.ic_svg_person)
                holder.tvCaption.text = context.getString(R.string.name)
                holder.tvVerify.visibility = View.GONE
            }
            "mobile" -> {
                holder.llProfileRow.setBackgroundResource(0)
                holder.ivIcon.setImageResource(R.drawable.ic_svg_mobile)
                holder.tvCaption.text = context.getString(R.string.mobile_number)
                holder.tvVerify.visibility = View.GONE
            }
            "email" -> {
                if (PlayerInfo.isEmailVerified()) {
                    holder.llProfileRow.setBackgroundResource(0)
                    holder.tvVerify.visibility = View.GONE
                }
                else
                    holder.tvVerify.visibility = View.VISIBLE
                holder.ivIcon.setImageResource(R.drawable.ic_svg_email)
                holder.tvCaption.text = context.getString(R.string.prompt_email)
            }
            "dob" -> {
                holder.llProfileRow.setBackgroundResource(0)
                holder.ivIcon.setImageResource(R.drawable.ic_svg_cake)
                holder.tvCaption.text = context.getString(R.string.date_of_birth)
                holder.tvVerify.visibility = View.GONE
            }
            "gender" -> {
                holder.llProfileRow.setBackgroundResource(0)
                if (mapInfo[item] == "M" || mapInfo[item].equals("Male", true))
                    holder.ivIcon.setImageResource(R.drawable.ic_svg_male)
                else if (mapInfo[item] == "F" || mapInfo[item].equals("Female", true))
                    holder.ivIcon.setImageResource(R.drawable.ic_svg_female)
                holder.tvCaption.text = context.getString(R.string.gender)
                holder.tvVerify.visibility = View.GONE
            }
            "address" -> {
                holder.llProfileRow.setBackgroundResource(0)
                holder.ivIcon.setImageResource(R.drawable.ic_svg_location)
                holder.tvCaption.text = context.getString(R.string.address)
                holder.tvVerify.visibility = View.GONE
            }
            "country" -> {
                holder.llProfileRow.setBackgroundResource(0)
                holder.ivIcon.setImageResource(R.drawable.ic_svg_location)
                holder.tvCaption.text = context.getString(R.string.country)
                holder.tvVerify.visibility = View.GONE
            }
        }

        holder.llProfileRow.setOnClickListener {
            if (item == "email" && !PlayerInfo.isEmailVerified()) onVerifyClick()
        }

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItem          = itemView.findViewById(R.id.tvItem) as MaterialTextView
        val tvVerify        = itemView.findViewById(R.id.tvVerify) as MaterialTextView
        val tvCaption       = itemView.findViewById(R.id.tvCaption) as MaterialTextView
        val ivIcon          = itemView.findViewById(R.id.ivIcon) as ShapeableImageView
        val llProfileRow    = itemView.findViewById(R.id.llProfileRow) as LinearLayoutCompat
    }

}