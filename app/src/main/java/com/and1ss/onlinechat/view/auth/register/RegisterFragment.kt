package com.studa.android.client.view.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.auth.login.LoginFragment

class RegisterFragment: Fragment() {
    private lateinit var backButton: ImageButton

    private lateinit var loginButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        backButton = view.findViewById(R.id.back_button)
        loginButton = view.findViewById(R.id.login_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        loginButton.setOnClickListener {
            (activity as? FragmentChanger)?.replaceFragment(LoginFragment.newInstance())
        }
    }

    companion object {
        fun newInstance(): RegisterFragment = RegisterFragment()
    }
}