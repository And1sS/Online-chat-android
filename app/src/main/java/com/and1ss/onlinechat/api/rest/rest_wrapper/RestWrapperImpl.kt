package com.and1ss.onlinechat.api.rest.rest_wrapper

import com.and1ss.onlinechat.api.model.AccountInfo
import com.and1ss.onlinechat.api.rest.ApiEndpoints
import com.google.gson.GsonBuilder
import com.and1ss.onlinechat.util.shared_preferences.SharedPreferencesWrapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


private const val BASE_URL = "http://10.0.2.2:8080/api/"
private const val TAG = "Repository"


class RestWrapperImpl
@Inject constructor(
    private val sharedPreferencesWrapper: SharedPreferencesWrapper
) : RestWrapper {
    private val retrofit: Retrofit
    private val api: ApiEndpoints

    private lateinit var accessToken: String
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

    override fun getApi(): ApiEndpoints = api

    override fun getAccessToken(): String = accessToken

    override fun saveAccessToken(accessToken: String) {
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

            accessToken?.let {
                newRequestBuilder.addHeader("Authorization", "Bearer $it")
            }

            val newRequest = newRequestBuilder.build()
            return chain.proceed(newRequest)
        }
    }
}