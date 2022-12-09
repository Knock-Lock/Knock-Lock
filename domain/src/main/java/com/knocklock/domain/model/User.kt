package com.knocklock.domain.model

data class User(
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
