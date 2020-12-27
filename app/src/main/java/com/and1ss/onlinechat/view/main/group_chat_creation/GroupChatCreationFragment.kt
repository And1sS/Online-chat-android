package com.and1ss.onlinechat.view.main.group_chat_creation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.util.stringToColor
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.main.HideShowIconInterface
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "GroupChatCreationFragm"

@AndroidEntryPoint
class GroupChatCreationFragment : Fragment() {
    private val viewModel: GroupChatCreationViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private var mutableList: MutableList<Pair<AccountInfo, Boolean>> = mutableListOf()

    private lateinit var creationButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var aboutEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_group_chat_creation, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        creationButton = view.findViewById(R.id.creation_button)
        titleEditText = view.findViewById(R.id.title_input)
        aboutEditText = view.findViewById(R.id.about_input)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setUpCreationButton()
        setUpTitleEditText()
        setUpAboutEditText()
        setUpRecyclerView()
        setUpObservers()
        setUpToolbar()
        loadFriends()
    }

    private fun loadFriends() = viewModel.getAcceptedFriends()

    private fun setUpObservers() {
        viewModel.participants.observe(viewLifecycleOwner) {
            mutableList.clear()
            mutableList.addAll(it)

            recyclerView.adapter!!.notifyDataSetChanged()
        }

        viewModel.creationConfirmation.observe(viewLifecycleOwner) {
            when (it) {
                GroupChatCreationViewModel.State.INITIAL -> return@observe
                GroupChatCreationViewModel.State.CREATED -> {
                    Toast.makeText(requireContext(), "Created!", Toast.LENGTH_LONG).show()
                    (requireActivity() as? FragmentChanger)?.navigateBack()
                }
                GroupChatCreationViewModel.State.FAILED ->
                    Toast.makeText(requireContext(), "Failed to create!", Toast.LENGTH_LONG).show()
                null -> {
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        recyclerView.adapter = ParticipantsAdapter(mutableList)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setUpAboutEditText() {
        aboutEditText.setText(viewModel.chatAbout)
        aboutEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.chatAbout = s.toString()
                creationButton.isEnabled = viewModel.creationReady
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun setUpTitleEditText() {
        titleEditText.setText(viewModel.chatTitle)
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.chatTitle = s.toString()
                creationButton.isEnabled = viewModel.creationReady
            }
        })
    }

    private fun setUpCreationButton() {
        creationButton.setOnClickListener { viewModel.createGroupChat() }
        creationButton.isEnabled = viewModel.creationReady
    }

    private fun setUpToolbar() {
        (requireActivity() as? HideShowIconInterface)?.showBackIcon()
        (requireActivity() as? AppCompatActivity)?.supportActionBar
            ?.setTitle(R.string.group_chat_creation_label)
    }

    companion object {
        fun newInstance(): GroupChatCreationFragment = GroupChatCreationFragment()
    }

    inner class ParticipantsAdapter(private val list: List<Pair<AccountInfo, Boolean>>) :
        RecyclerView.Adapter<ParticipantsAdapter.ParticipantItemHolder>() {
        inner class ParticipantItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private lateinit var participant: Pair<AccountInfo, Boolean>
            private val image: ImageView = itemView.findViewById(R.id.image_profile)
            private val profileTextView: TextView = itemView.findViewById(R.id.profile_label)
            private val profileLoginTextView: TextView = itemView.findViewById(R.id.profile_login)
            private val selectedCheckBox: CheckBox = itemView.findViewById(R.id.checkbox)

            init {
                selectedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    participant = Pair(participant.first, isChecked)
                    viewModel.setSelection(participant)
                    creationButton.isEnabled = viewModel.creationReady
                }
            }

            fun bind(participant: Pair<AccountInfo, Boolean>) {
                this.participant = participant
                val user = participant.first
                image.setImageDrawable(
                    TextDrawable.builder()
                        .buildRound(user.initials, stringToColor(user.nameSurname))
                )
                profileTextView.text = user.nameSurname
                profileLoginTextView.text = user.login
                selectedCheckBox.isChecked = participant.second
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantItemHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.group_chat_participants_selection_item, parent, false)

            return ParticipantItemHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ParticipantItemHolder, position: Int) =
            holder.bind(list[position])
    }
}