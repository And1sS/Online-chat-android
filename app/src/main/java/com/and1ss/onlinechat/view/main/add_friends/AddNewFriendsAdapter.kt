package com.and1ss.onlinechat.view.main.add_friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.util.stringToColor

class AddNewFriendsAdapter(
    private val list: List<AccountInfo>,
    private val callback: AddFriendsCallback
) : RecyclerView.Adapter<AddNewFriendsAdapter.FriendItemHolder>() {
    interface AddFriendsCallback {
        fun sendFriendRequest(friendId: String)
        fun getCurrentContext(): Context
        fun getCurrentUserAccount(): AccountInfo
    }

    inner class FriendItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image_profile)
        private val profileTextView: TextView = itemView.findViewById(R.id.profile_label)
        private val profileLoginTextView: TextView = itemView.findViewById(R.id.profile_login)
        private val statusButton: Button = itemView.findViewById(R.id.status_button)

        fun bind(friend: AccountInfo) {
            image.setImageDrawable(
                TextDrawable.builder()
                    .buildRound(friend.initials, stringToColor(friend.nameSurname))
            )
            profileTextView.text = friend.nameSurname
            profileLoginTextView.text = friend.login

            statusButton.text = "Add"
            statusButton.setOnClickListener {
                callback.sendFriendRequest(friend.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendItemHolder {
        val view = LayoutInflater.from(callback.getCurrentContext())
            .inflate(R.layout.friend_list_item, parent, false)

        return FriendItemHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FriendItemHolder, position: Int) =
        holder.bind(list[position])
}