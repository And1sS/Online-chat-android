package com.and1ss.onlinechat.view.main.friends

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
import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO
import com.and1ss.onlinechat.api.dto.FriendshipStatus
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.util.stringToColor

class FriendsAdapter(
    private val list: List<FriendRetrievalDTO>,
    private val callback: FriendsCallback
) :
    RecyclerView.Adapter<FriendsAdapter.FriendItemHolder>() {

    interface FriendsCallback {
        fun acceptFriendRequest(userId: String)
        fun deleteFriend(userId: String)
        fun getCurrentContext(): Context
        fun getCurrentUserAccount(): AccountInfo
    }

    inner class FriendItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image_profile)
        private val profileTextView: TextView = itemView.findViewById(R.id.profile_label)
        private val profileLoginTextView: TextView = itemView.findViewById(R.id.profile_login)
        private val statusButton: Button = itemView.findViewById(R.id.status_button)

        fun bind(friendDTO: FriendRetrievalDTO) {
            val currentUserAccount = callback.getCurrentUserAccount()
            val isRequestIssuer = friendDTO.isRequestIssuer(currentUserAccount.id)
            val isRequestee = friendDTO.isRequestee(currentUserAccount.id)
            val friend = if (isRequestIssuer) friendDTO.requestee else friendDTO.requestIssuer
            val initials = friend?.initials ?: ""
            val nameSurname = (friend?.name ?: "") + " " + friend?.surname

            image.setImageDrawable(
                TextDrawable.builder().buildRound(initials, stringToColor(nameSurname))
            )
            profileTextView.text = nameSurname
            friend?.login?.let {
                profileLoginTextView.text = friend.login!!
            }

            statusButton.text = when {
                isRequestee && (friendDTO.status?.equals(FriendshipStatus.pending) != false) -> "Accept"
                isRequestIssuer && (friendDTO.status?.equals(FriendshipStatus.pending) != false) -> "Decline"
                else -> "Delete"
            }
            statusButton.setOnClickListener {
                statusButtonOnClickListener(friend, friendDTO.status, isRequestee)
            }
        }

        private fun statusButtonOnClickListener(
            friend: AccountInfoRetrievalDTO?,
            status: FriendshipStatus?,
            isRequestee: Boolean
        ) {
            friend?.id?.let {
                if (isRequestee && (status?.equals(FriendshipStatus.pending) != false)) {
                    callback.acceptFriendRequest(friend.id!!)
                } else {
                    callback.deleteFriend(friend.id!!)
                }
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