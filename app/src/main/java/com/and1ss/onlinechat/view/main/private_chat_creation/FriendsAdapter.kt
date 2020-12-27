package com.and1ss.onlinechat.view.main.private_chat_creation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.util.stringToColor

class FriendsAdapter(context: Context, list: List<AccountInfo>) :
    ArrayAdapter<AccountInfo>(context, 0, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.private_chat_creation_list_item, parent, false
        )

        val profileImage = view.findViewById<ImageView>(R.id.image_profile)
        val color = item?.nameSurname?.let { stringToColor(it) } ?: 0
        profileImage.setImageDrawable(
            TextDrawable.builder().buildRound(item?.initials, color)
        )

        view.findViewById<TextView>(R.id.profile_label).apply {
            text = item?.nameSurname ?: ""
        }
        view.findViewById<TextView>(R.id.profile_login).apply {
            text = item?.login ?: ""
        }

        return view
    }
}