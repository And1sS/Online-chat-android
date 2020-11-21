package com.and1ss.onlinechat.view.main.group_chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.and1ss.onlinechat.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "GroupChatFragment"

@AndroidEntryPoint
class GroupChatFragment : Fragment() {
    private val viewModel: GroupChatViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView

    private lateinit var messageEditText: EditText

    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatId = arguments?.getString(ARG_CHAT_ID)
            ?: throw IllegalStateException("Chat id must be specified")

        viewModel.chatId = chatId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_chat, container, false)

        messageEditText = view.findViewById(R.id.message_input)
        sendButton = view.findViewById(R.id.send_message_button)
        recyclerView = view.findViewById(R.id.recycler_view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.messageString = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        messageEditText.setText(viewModel.messageString)

        sendButton.setOnClickListener {
            viewModel.send(viewModel.messageString)
            messageEditText.setText("")
        }

        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                1, StaggeredGridLayoutManager.VERTICAL
            ).apply {
                reverseLayout = true
            }
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = ChatMessagesAdapter(
                list = viewModel.chatMessages,
                context = requireContext(),
                me = viewModel.myAccount
            )
        }

        viewModel.notifier.observe(viewLifecycleOwner) { event ->
            when (event) {
                is Event.LoadedInitial -> {
                    recyclerView.adapter?.notifyDataSetChanged()
                    recyclerView.layoutManager?.scrollToPosition(0)
                }

                is Event.MessageAdd -> {
                    recyclerView.adapter?.notifyDataSetChanged()
                    recyclerView.layoutManager?.scrollToPosition(0)
                }
                else -> Log.d(TAG, "onEvent: $event")
            }
        }

        lifecycle.coroutineScope.launch {
            viewModel.getAllMessages()
        }
    }

    companion object {
        private const val ARG_CHAT_ID = "CHAT_ID"

        fun newInstance(chatId: String): Fragment =
            GroupChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHAT_ID, chatId)
                }
            }
    }
}
