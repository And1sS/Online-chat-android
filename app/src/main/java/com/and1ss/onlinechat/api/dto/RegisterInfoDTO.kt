package com.and1ss.onlinechat.api.dto

data class RegisterInfoDTO(
    val login: String,
    val password: String,
    val name: String,
    val surname: String
)