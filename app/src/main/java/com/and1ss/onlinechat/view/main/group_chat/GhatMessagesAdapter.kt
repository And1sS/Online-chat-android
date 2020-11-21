package com.and1ss.onlinechat.view.main.group_chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.dto.GroupMessageRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo

class ChatMessagesAdapter(
    private val list: List<GroupMessageRetrievalDTO>,
    private val context: Context,
    private val me: AccountInfo
) :
    RecyclerView.Adapter<ChatMessagesAdapter.MessageViewHolder>() {

    open class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bind(message: GroupMessageRetrievalDTO) {}

        class GroupMessageLeftItemHolder(itemView: View) : MessageViewHolder(itemView) {
            private val authorTextView: TextView =
                itemView.findViewById(R.id.message_author_label)
            private val contentTextView: TextView =
                itemView.findViewById(R.id.message_body_label)
            private val messageTimeTextView: TextView =
                itemView.findViewById(R.id.message_time_label)

            override fun bind(message: GroupMessageRetrievalDTO) {
                authorTextView.text =
                    message.author?.name ?: "" + " " + message.author?.surname ?: ""

                messageTimeTextView.text =
                    if (message.createdAt != null) {
                        message.createdAt.toString()
                    } else {
                        ""
                    }

                contentTextView.text = message.contents
            }
        }

        class GroupMessageRightItemHolder(itemView: View) : MessageViewHolder(itemView) {
            private val contentTextView: TextView =
                itemView.findViewById(R.id.message_body_label)
            private val messageTimeTextView: TextView =
                itemView.findViewById(R.id.message_time_label)

            override fun bind(message: GroupMessageRetrievalDTO) {
                messageTimeTextView.text =
                    if (message.createdAt != null) {
                        message.createdAt.toString()
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
                )
            )

            VIEW_TYPE_RIGHT -> MessageViewHolder.GroupMessageRightItemHolder(
                layoutInflater.inflate(
                    R.layout.group_chat_message_right, parent, false
                )
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

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
        holder.bind(list[position])

    companion object {
        private const val VIEW_TYPE_LEFT = 0
        private const val VIEW_TYPE_RIGHT = 1
    }
}