package com.and1ss.onlinechat.api.rest

import com.and1ss.onlinechat.api.dto.LoginInfoDTO
import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.util.shared_preferences.SharedPreferencesWrapper
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


//private const val BASE_URL = "http://10.0.2.2:8080/api/"
private const val BASE_URL = "http://176.36.243.160:8080/api/"
private const val TAG = "Repository"


class RestWrapperImpl
@Inject constructor(
    private val sharedPreferencesWrapper: SharedPreferencesWrapper
) : RestWrapper {
    private val retrofit: Retrofit
    private val api: ApiEndpoints

    private var accessToken: String = ""
    private lateinit var myAccount: AccountInfo

    init {
        val gson = GsonBuilder().create()

        val httpClient = OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        retrofit = Retrofit
            .Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit.create(ApiEndpoints::class.java)
    }

    override fun getApi(): ApiEndpoints {
        if (accessToken.isEmpty()) {
            throw IllegalStateException("Api consuming without logging")
        }
        return api
    }

    override suspend fun login(loginCredentials: LoginInfoDTO) =
        withContext(Dispatchers.IO) {
            saveAccessToken(api.login(loginCredentials).mapToAccessTokenOrThrow())
            saveMyAccount(api.getMyAccount().mapToAccountInfoOrThrow())
        }

    override fun getAccessToken(): String = accessToken

    override suspend fun saveAccessToken(accessToken: String) {
        this.accessToken = accessToken
        sharedPreferencesWrapper.saveAccessToken(accessToken)
    }

    override fun saveMyAccount(accountInfo: AccountInfo) {
        this.myAccount = accountInfo
    }

    override fun getMyAccount(): AccountInfo = myAccount

    inner class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val newRequestBuilder = chain.request().newBuilder()

            if (accessToken.isNotEmpty()) {
                newRequestBuilder.addHeader("Authorization", "Bearer $accessToken")
            }

            val newRequest = newRequestBuilder.build()
            return chain.proceed(newRequest)
        }
    }
}