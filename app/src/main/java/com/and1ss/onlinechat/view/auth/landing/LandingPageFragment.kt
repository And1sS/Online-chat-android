package com.and1ss.onlinechat.view.auth.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.auth.login.LoginFragment
import com.studa.android.client.view.auth.register.RegisterFragment

class LandingPageFragment : Fragment() {
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_landing, container, false)

        loginButton = view.findViewById(R.id.login_button) as Button
        registerButton = view.findViewById(R.id.register_button) as Button

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            transitToAnotherFragment(LoginFragment.newInstance())
        }

        registerButton.setOnClickListener {
            transitToAnotherFragment(RegisterFragment.newInstance())
        }
    }

    private fun transitToAnotherFragment(fragment: Fragment) {
        (activity as? FragmentChanger)?.transitToFragment(fragment)
    }

    companion object {
        fun newInstance(): LandingPageFragment = LandingPageFragment()
    }
}