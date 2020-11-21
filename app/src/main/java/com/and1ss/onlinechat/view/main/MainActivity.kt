package com.and1ss.onlinechat.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.view.auth.ActivityChanger
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.main.group_chat_list.GroupChatsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentChanger, ActivityChanger {
    @Inject
    lateinit var webSocketWrapper: WebSocketWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webSocketWrapper.connect()
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GroupChatsFragment.newInstance())
                .commit()
        }
    }

    override fun transitToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_open_enter,
                R.anim.fragment_open_exit,
                R.anim.fragment_close_enter,
                R.anim.fragment_close_exit
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_open_enter,
                R.anim.fragment_open_exit,
                R.anim.fragment_close_enter,
                R.anim.fragment_close_exit
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun transitToActivity(intent: Intent) {
        startActivity(intent)
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, MainActivity::class.java)
    }
}