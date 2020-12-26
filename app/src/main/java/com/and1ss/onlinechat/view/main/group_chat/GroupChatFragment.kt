package com.and1ss.onlinechat.view.main.group_chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.model.GroupChat
import com.and1ss.onlinechat.view.main.HideShowIconInterface
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "GroupChatFragment"

private const val MAX_TITLE_LENGTH = 20

@AndroidEntryPoint
class GroupChatFragment : Fragment() {
    private val viewModel: GroupChatViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView

    private lateinit var messageEditText: EditText

    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chat = arguments?.getParcelable<GroupChat>(ARG_CHAT)
            ?: throw IllegalStateException("Chat id must be specified")

        viewModel.chat = chat
        viewModel.connect()
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

        setUpObservers()

        setUpToolbar()
        setUpMessageInput()
        setUpRecyclerView()
        setUpSendButton()

        viewModel.getAllMessages()
    }

    private fun setUpSendButton() {
        sendButton.setOnClickListener {
            viewModel.send(viewModel.messageString)
            messageEditText.setText("")
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpToolbar() {
        val title = if (viewModel.chat.title.length > MAX_TITLE_LENGTH) {
            viewModel.chat.title.substring(0..MAX_TITLE_LENGTH) + "..."
        } else {
            viewModel.chat.title
        }
        (requireActivity() as? HideShowIconInterface)?.showBackIcon()
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = title
    }

    private fun setUpMessageInput() {
        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.messageString = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        messageEditText.setText(viewModel.messageString)
    }

    private fun setUpRecyclerView() {
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
    }

    private fun setUpObservers() {
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
    }

    companion object {
        private const val ARG_CHAT = "CHAT"

        fun newInstance(chat: GroupChat): Fragment =
            GroupChatFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CHAT, chat)
                }
            }
    }
}
