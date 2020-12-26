package com.and1ss.onlinechat.view.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.and1ss.onlinechat.api.rest.RestWrapper
import com.and1ss.onlinechat.util.shared_preferences.SharedPreferencesWrapper
import com.and1ss.onlinechat.view.auth.AuthenticationActivity
import com.and1ss.onlinechat.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
    @Inject
    lateinit var restWrapper: RestWrapper

    @Inject
    lateinit var sharedPreferencesWrapper: SharedPreferencesWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.coroutineScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                try {
                    val token = sharedPreferencesWrapper.getAccessToken()
                    restWrapper.saveAccessToken(token)


                    val myAccount = restWrapper.getApi()
                        .getMyAccount().mapToAccountInfoOrThrow()
                    restWrapper.saveMyAccount(myAccount)

                    startActivity(MainActivity::class.java)
                } catch (e: ConnectException) {
                    startActivity(AuthenticationActivity::class.java)
                } catch (e: Exception) {
                    startActivity(AuthenticationActivity::class.java)
                }
            }
        }
    }

    private fun <T> startActivity(clazz: Class<T>) {
        val intent = Intent(this, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        startActivity(intent)
    }
}