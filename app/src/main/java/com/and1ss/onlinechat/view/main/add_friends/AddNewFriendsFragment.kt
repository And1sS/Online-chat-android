package com.and1ss.onlinechat.view.main.add_friends

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.dto.AccountInfoRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.RestWrapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddNewFriendsFragment : Fragment(), AddNewFriendsAdapter.AddFriendsCallback {
    @Inject
    lateinit var restWrapper: RestWrapper

    private val viewModel: AddNewFriendsViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private var mutableList: MutableList<AccountInfoRetrievalDTO> = mutableListOf()

    private lateinit var backButton: ImageButton
    private lateinit var searchButton: Button

    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_friends, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        backButton = view.findViewById(R.id.back_button)
        searchButton = view.findViewById(R.id.search_button)
        editText = view.findViewById(R.id.search_input)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setUpSearchButton()
        setUpEditText()
        loadInitialData()
        setUpObservers()
        setUpToolbar()
    }

    private fun setUpEditText() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.loginLike = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        editText.setText(viewModel.loginLike)
    }

    private fun setUpRecyclerView() {
        recyclerView.adapter = AddNewFriendsAdapter(mutableList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setUpToolbar() {
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setUpObservers() {
        viewModel.friends.observe(viewLifecycleOwner) {
            mutableList.clear()
            mutableList.addAll(it)

            recyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpSearchButton() {
        searchButton.setOnClickListener {
            viewModel.getUsersWhoAreNotCurrentUserFriendsWithLoginLike()
        }
    }

    private fun loadInitialData() =
        viewModel.getUsersWhoAreNotCurrentUserFriendsWithLoginLike()

    override fun sendFriendRequest(friendId: String) {
        viewModel.sendFriendRequest(friendId)
    }

    override fun getCurrentContext(): Context = requireContext()

    override fun getCurrentUserAccount(): AccountInfo = restWrapper.getMyAccount()

    companion object {
        fun newInstance(): Fragment = AddNewFriendsFragment()
    }
}

