package com.and1ss.onlinechat.view.main.private_chat_creation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.and1ss.onlinechat.R
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "PrivateChatCreationDial"

@AndroidEntryPoint
class PrivateChatCreationDialogFragment : DialogFragment() {
    private val viewModel: PrivateChatCreationViewModel by viewModels()

    private lateinit var spinner: Spinner
    private lateinit var cancelButton: Button
    private lateinit var creationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = requireActivity().layoutInflater.inflate(
            R.layout.fragment_private_chat_creation,
            LinearLayout(activity), false
        )

        spinner = view.findViewById(R.id.spinner)
        cancelButton = view.findViewById(R.id.cancel_button)
        creationButton = view.findViewById(R.id.creation_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFriends()
        viewModel.friends.observe(viewLifecycleOwner) {
            FriendsAdapter(requireContext(), it).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            spinner.setSelection(viewModel.selected)
        }

        viewModel.creationConfirmation.observe(viewLifecycleOwner) {
            when (it) {
                PrivateChatCreationViewModel.State.INITIAL -> {
                }
                PrivateChatCreationViewModel.State.FAILED -> {
                    Toast.makeText(
                        requireContext(), "Failed to create private chat",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrivateChatCreationViewModel.State.CREATED -> {
                    targetFragment?.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        Intent()
                    )
                    dismiss()
                }
            }
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?, selectedItemView: View,
                position: Int, id: Long
            ) {
                viewModel.selected = position
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        cancelButton.setOnClickListener { dismiss() }
        creationButton.setOnClickListener { viewModel.createGroupChat() }

    }

    companion object {
        fun newInstance(): DialogFragment = PrivateChatCreationDialogFragment()
    }
}