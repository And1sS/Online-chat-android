package com.and1ss.onlinechat.view.main.group_chat_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.dto.GroupChatRetrievalDTO
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.main.group_chat.GroupChatFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatsFragment : Fragment() {
    private val viewModel: GroupChatsViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private var mutableList: MutableList<GroupChatRetrievalDTO> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = ChatsAdapter(mutableList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.chats.observe(viewLifecycleOwner) {
            mutableList.clear()
            mutableList.addAll(it)

            recyclerView.adapter!!.notifyDataSetChanged()
        }

        lifecycle.coroutineScope.launch {
            viewModel.getChats()
        }
    }

    companion object {
        fun newInstance() = GroupChatsFragment()
    }

    inner class ChatsAdapter(private val list: List<GroupChatRetrievalDTO>) :
        RecyclerView.Adapter<ChatsAdapter.GroupChatItemHolder>() {
        inner class GroupChatItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private lateinit var chat: GroupChatRetrievalDTO
            private val chatTitleTextView: TextView = itemView.findViewById(R.id.chat_title_label)
            private val chatLastMessageTextView: TextView =
                itemView.findViewById(R.id.last_message_label)
            private val chatLastMessageTimeTextView: TextView =
                itemView.findViewById(R.id.last_message_time_label)

            init {
                itemView.setOnClickListener {
                    (activity as? FragmentChanger)?.transitToFragment(
                        GroupChatFragment.newInstance(chat.mapToGroupChatOrThrow())
                    )
                }
            }

            fun bind(chat: GroupChatRetrievalDTO) {
                chatTitleTextView.text = chat.title

                chatLastMessageTimeTextView.text = if (chat.lastMessage?.createdAt != null) {
                    chat.lastMessage!!.createdAt.toString()
                } else {
                    ""
                }

                chatLastMessageTextView.text = if (chat.lastMessage != null) {
                    chat.lastMessage!!.contents
                } else {
                    resources.getString(R.string.no_messages_yet_label)
                }

                this.chat = chat
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatItemHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.group_chat_list_item, parent, false)

            return GroupChatItemHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: GroupChatItemHolder, position: Int) =
            holder.bind(list[position])
    }
}

