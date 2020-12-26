package com.and1ss.onlinechat.view.main.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.dto.FriendRetrievalDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.main.add_friends.AddNewFriendsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment : Fragment(), FriendsAdapter.FriendsCallback {
    @Inject
    lateinit var restWrapper: RestWrapper

    private val viewModel: FriendsViewModel by viewModels()

    private lateinit var addButton: FloatingActionButton

    private lateinit var recyclerView: RecyclerView
    private var mutableList: MutableList<FriendRetrievalDTO> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        addButton = view.findViewById(R.id.add_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = FriendsAdapter(mutableList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.friends.observe(viewLifecycleOwner) {
            mutableList.clear()
            mutableList.addAll(it)

            recyclerView.adapter!!.notifyDataSetChanged()
        }
        viewModel.getFriends()

        addButton.setOnClickListener {
            (activity as? FragmentChanger)?.transitToFragment(
                AddNewFriendsFragment.newInstance()
            )
        }
    }

    companion object {
        fun newInstance(): Fragment = FriendsFragment()
    }

    override fun acceptFriendRequest(userId: String) {
        viewModel.acceptFriendRequest(userId)
    }

    override fun deleteFriend(userId: String) {
        viewModel.deleteFriend(userId)
    }

    override fun getCurrentContext(): Context = requireContext()

    override fun getCurrentUserAccount(): AccountInfo =
        restWrapper.getMyAccount()
}