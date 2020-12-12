package com.and1ss.onlinechat.view.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.rest_wrapper.RestWrapper
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
        //setTheme(R.style.SplashScreenTheme)
        super.onCreate(savedInstanceState)

        lifecycle.coroutineScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                try {
                    val token = sharedPreferencesWrapper.getAccessToken()
                    restWrapper.saveAccessToken(token)


                    val myAccount = restWrapper.getApi().getMyAccount()
                    restWrapper.saveMyAccount(
                        AccountInfo(
                            id = myAccount.id!!,
                            name = myAccount.name!!,
                            surname = myAccount.surname!!
                        )
                    )

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