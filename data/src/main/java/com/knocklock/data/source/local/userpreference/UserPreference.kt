package com.knocklock.data.source.local.userpreference

import kotlinx.serialization.Serializable

@Serializable
data class UserPreference(
    val authenticationType: AuthenticationType,
    val password: String
)

enum class AuthenticationType {
    GESTURE, PASSWORD;

    companion object {
        fun getValue(value: String) =
            values().find { value == it.name } ?: GESTURE
    }
}