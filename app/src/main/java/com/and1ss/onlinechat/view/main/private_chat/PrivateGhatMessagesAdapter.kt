package com.and1ss.onlinechat.view.main.private_chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.dto.PrivateMessageRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.util.stringToColor
import java.text.SimpleDateFormat

class PrivateGhatMessagesAdapter(
    private val list: List<PrivateMessageRetrievalDTO>,
    private val context: Context,
    private val me: AccountInfo
) :
    RecyclerView.Adapter<PrivateGhatMessagesAdapter.MessageViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

    open class MessageViewHolder(itemView: View, val dateFormat: SimpleDateFormat) :
        RecyclerView.ViewHolder(itemView) {
        open fun bind(message: PrivateMessageRetrievalDTO) {}

        class GroupMessageLeftItemHolder(itemView: View, dateFormat: SimpleDateFormat) :
            MessageViewHolder(itemView, dateFormat) {
            private val image: ImageView = itemView.findViewById(R.id.image_message_profile)
            private val authorTextView: TextView = itemView.findViewById(R.id.message_author_label)
            private val contentTextView: TextView = itemView.findViewById(R.id.message_body_label)
            private val messageTimeTextView: TextView =
                itemView.findViewById(R.id.message_time_label)

            override fun bind(message: PrivateMessageRetrievalDTO) {
                val initials = message.author?.initials ?: ""
                val nameSurname = (message.author?.name ?: "") + " " + message.author?.surname

                image.setImageDrawable(
                    TextDrawable.builder()
                        .buildRound(initials, stringToColor(nameSurname))
                )
                authorTextView.text = nameSurname
                contentTextView.text = message.contents
                messageTimeTextView.text = if (message.createdAt != null) {
                    dateFormat.format(message.createdAt).toString()
                } else {
                    ""
                }
            }
        }

        class GroupMessageRightItemHolder(itemView: View, dateFormat: SimpleDateFormat) :
            MessageViewHolder(itemView, dateFormat) {
            private val contentTextView: TextView = itemView.findViewById(R.id.message_body_label)
            private val messageTimeTextView: TextView =
                itemView.findViewById(R.id.message_time_label)

            override fun bind(message: PrivateMessageRetrievalDTO) {
                messageTimeTextView.text = if (message.createdAt != null) {
                    dateFormat.format(message.createdAt).toString() + " "
                } else {
                    ""
                }
                contentTextView.text = message.contents
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return when (viewType) {
            VIEW_TYPE_LEFT -> MessageViewHolder.GroupMessageLeftItemHolder(
                layoutInflater.inflate(
                    R.layout.group_chat_message_left, parent, false
                ), dateFormat
            )

            VIEW_TYPE_RIGHT -> MessageViewHolder.GroupMessageRightItemHolder(
                layoutInflater.inflate(
                    R.layout.group_chat_message_right, parent, false
                ), dateFormat
            )

            else -> throw IllegalStateException("Incorrect view type: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (list[position].author?.id?.equals(me.id) == true) {
            VIEW_TYPE_RIGHT
        } else {
            VIEW_TYPE_LEFT
        }


    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        synchronized(list) {
            if (position in list.indices) {
                holder.bind(list[position])
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_LEFT = 0
        private const val VIEW_TYPE_RIGHT = 1
    }
}