package com.and1ss.onlinechat.util

import android.graphics.Color

fun stringToColor(string: String): Int {
    val hash: Int = string.hashCode()
    val r = hash and 0xFF0000 shr 16
    val g = hash and 0x00FF00 shr 8
    val b = hash and 0x0000FF

    return Color.rgb(r, g, b)
}