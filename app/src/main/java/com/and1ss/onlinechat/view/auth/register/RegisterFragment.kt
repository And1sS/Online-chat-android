package com.and1ss.onlinechat.view.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.auth.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var backButton: ImageButton
    private lateinit var loginButton: TextView
    private lateinit var registerButton: Button

    private lateinit var loginEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        backButton = view.findViewById(R.id.back_button)
        loginButton = view.findViewById(R.id.login_button)
        registerButton = view.findViewById(R.id.register_button)
        loginEditText = view.findViewById(R.id.login_input)
        nameEditText = view.findViewById(R.id.first_name_input)
        surnameEditText = view.findViewById(R.id.last_name_input)
        passwordEditText = view.findViewById(R.id.password_input)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener { activity?.onBackPressed() }
        loginButton.setOnClickListener {
            (activity as? FragmentChanger)?.replaceFragment(LoginFragment.newInstance())
        }

        registerButton.setOnClickListener {
            viewModel.login = loginEditText.text.toString()
            viewModel.password = passwordEditText.text.toString()
            viewModel.name = nameEditText.text.toString()
            viewModel.surname = surnameEditText.text.toString()

            viewModel.register()
        }

        viewModel.registrationConfirmation.observe(viewLifecycleOwner) {
            when (it) {
                RegisterViewModel.State.FAILED ->
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                RegisterViewModel.State.REGISTERED -> {
                    Toast.makeText(requireContext(), "Registered!", Toast.LENGTH_SHORT).show()
                    (requireActivity() as FragmentChanger).replaceFragment(LoginFragment.newInstance())
                }
                RegisterViewModel.State.INITIAL -> {
                }
                else -> {
                }
            }
        }
    }

    companion object {
        fun newInstance(): RegisterFragment = RegisterFragment()
    }
}