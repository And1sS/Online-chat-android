package com.and1ss.onlinechat.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.and1ss.onlinechat.R
import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.api.ws.WebSocketWrapper
import com.and1ss.onlinechat.view.auth.ActivityChanger
import com.and1ss.onlinechat.view.auth.FragmentChanger
import com.and1ss.onlinechat.view.main.friends.FriendsFragment
import com.and1ss.onlinechat.view.main.group_chat_list.GroupChatsFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentChanger, ActivityChanger, HideShowIconInterface {
    @Inject
    lateinit var webSocketWrapper: WebSocketWrapper

    @Inject
    lateinit var restWrapper: RestWrapper

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var navDrawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var nameSurnameTextView: TextView
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViewReferences()
        initToolbar()
        initNavigationDrawer()
        setUpProfileTextViews()

        initWebSocketConnection()
        initStartingFragment()
    }

    private fun setUpViewReferences() {
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        val hView: View = navigationView.getHeaderView(0)
        nameSurnameTextView = hView.findViewById(R.id.name_surname_title)
        loginTextView = hView.findViewById(R.id.login_title)
    }

    private fun setUpProfileTextViews() {
        nameSurnameTextView.text = restWrapper.getMyAccount().nameSurname
        loginTextView.text = restWrapper.getMyAccount().login
    }

    private fun initWebSocketConnection() {
        if (!webSocketWrapper.isConnected()) {
            webSocketWrapper.connect()
        }
    }

    private fun initStartingFragment() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GroupChatsFragment.newInstance())
                .commit()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initNavigationDrawer() {
        findViewById<NavigationView>(R.id.nav_view).apply {
            setNavigationItemSelectedListener { onNavigationItemSelected(it) }
            setCheckedItem(R.id.nav_group_chats)
        }

        navDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.nav_drawer_open, R.string.nav_drawer_close
        ).apply {
            setToolbarNavigationClickListener { onBackPressed() }
        }
        drawerLayout.apply { addDrawerListener(navDrawerToggle) }
        navDrawerToggle.syncState()
    }

    private fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val lastState = viewModel.state
        viewModel.state = when (menuItem.itemId) {
            R.id.nav_friends -> MainActivityViewModel.Screens.FRIENDS
            R.id.nav_group_chats -> MainActivityViewModel.Screens.GROUP_CHATS
            else -> viewModel.state
        }

        if (viewModel.state != lastState) {
            val newFragment = when (viewModel.state) {
                MainActivityViewModel.Screens.FRIENDS -> FriendsFragment.newInstance()
                MainActivityViewModel.Screens.GROUP_CHATS -> GroupChatsFragment.newInstance()
                else -> FriendsFragment.newInstance()
            }
            replaceFragment(newFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
            clearBackStack()
        }

        return true
    }

    private fun clearBackStack() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun transitToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun replaceFragment(fragment: Fragment) {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun navigateBack() {
        onBackPressed()
    }

    override fun transitToActivity(intent: Intent) = startActivity(intent)

    override fun showHamburgerIcon() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        navDrawerToggle.isDrawerIndicatorEnabled = true
    }

    override fun showBackIcon() {
        navDrawerToggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        fun newIntent(packageContext: Context) =
            Intent(packageContext, MainActivity::class.java)
    }
}

interface HideShowIconInterface {
    fun showHamburgerIcon()
    fun showBackIcon()
}