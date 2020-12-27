package com.and1ss.onlinechat.view.main.private_chat_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.dto.PrivateChatRetrievalDTO
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.main.HideShowIconInterface
import com.and1ss.onlinechat.view.main.private_chat.PrivateChatFragment
import com.and1ss.onlinechat.view.main.private_chat_creation.PrivateChatCreationDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PrivateChatsFragment"

@AndroidEntryPoint
class PrivateChatsFragment : Fragment() {
    private val viewModel: PrivateChatsViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private var mutableList: MutableList<PrivateChatRetrievalDTO> = mutableListOf()

    private lateinit var addButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_chats, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        addButton = view.findViewById(R.id.add_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setUpRecyclerView()
        setUpObservers()
        setUpToolbar()
        setUpAddButton()

        viewModel.getChats()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CREATE_PRIVATE_CHAT -> viewModel.getChats()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setUpAddButton() {
        addButton.setOnClickListener {
            val dialog: DialogFragment = PrivateChatCreationDialogFragment.newInstance()
            dialog.setTargetFragment(this, REQUEST_CREATE_PRIVATE_CHAT)
            dialog.show(parentFragmentManager, "TEST")
        }
    }

    private fun setUpToolbar() {
        (requireActivity() as? HideShowIconInterface)?.showHamburgerIcon()
        (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.private_chats_label)
    }

    private fun setUpObservers() {
        viewModel.chats.observe(viewLifecycleOwner) {
            mutableList.clear()
            mutableList.addAll(it)

            recyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView() {
        recyclerView.adapter = PrivateChatsAdapter(mutableList)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        const val REQUEST_CREATE_PRIVATE_CHAT = 1

        fun newInstance(): PrivateChatsFragment = PrivateChatsFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (requireActivity() as? FragmentChanger)?.navigateBack()
        return true
    }

    inner class PrivateChatsAdapter(private val list: List<PrivateChatRetrievalDTO>) :
        RecyclerView.Adapter<PrivateChatsAdapter.PrivateChatItemHolder>() {
        inner class PrivateChatItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private lateinit var chat: PrivateChatRetrievalDTO
            private val chatTitleTextView: TextView = itemView.findViewById(R.id.chat_title_label)
            private val chatLastMessageTextView: TextView =
                itemView.findViewById(R.id.last_message_label)
            private val chatLastMessageTimeTextView: TextView =
                itemView.findViewById(R.id.last_message_time_label)

            init {
                itemView.setOnClickListener {
                    (activity as? FragmentChanger)?.transitToFragment(
                        PrivateChatFragment.newInstance(chat.mapToPrivateChatOrThrow())
                    )
                }
            }

            fun bind(chat: PrivateChatRetrievalDTO) {
                chatTitleTextView.text = if (chat.isCompleted) {
                    chat.mapToPrivateChatOrThrow()
                        .getTitle(viewModel.myAccount)
                } else {
                    ""
                }

                chatLastMessageTimeTextView.text =
                    chat.lastMessage?.createdAt?.toString() ?: ""

                chatLastMessageTextView.text = chat.lastMessage?.contents
                    ?: resources.getString(R.string.no_messages_yet_label)

                this.chat = chat
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateChatItemHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.group_chat_list_item, parent, false)

            return PrivateChatItemHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: PrivateChatItemHolder, position: Int) =
            holder.bind(list[position])
    }
}