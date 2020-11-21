package com.and1ss.onlinechat.util

import com.google.gson.Gson

inline fun <reified T> fromJson(json: String): T =
    Gson().fromJson(json, T::class.java)

inline fun <reified T> toJson(obj: T): String =
    Gson().toJson(obj, T::class.java)