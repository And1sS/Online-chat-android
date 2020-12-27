package com.and1ss.onlinechat.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.view.auth.landing.LandingPageFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AuthenticationActivity"

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), FragmentChanger, ActivityChanger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LandingPageFragment.newInstance())
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
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
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

    override fun navigateBack() {
        navigateBack()
    }

    override fun transitToActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun <T> startActivity(clazz: Class<T>) {
        val intent = Intent(this, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
        startActivity(intent)
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, AuthenticationActivity::class.java)
    }
}

interface FragmentChanger {
    fun transitToFragment(fragment: Fragment)
    fun replaceFragment(fragment: Fragment)
    fun navigateBack()
}

interface ActivityChanger {
    fun transitToActivity(intent: Intent)
    fun <T> startActivity(clazz: Class<T>)
}