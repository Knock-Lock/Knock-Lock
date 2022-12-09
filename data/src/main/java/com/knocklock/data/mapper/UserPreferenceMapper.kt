package com.knocklock.data.mapper

import com.knocklock.data.source.local.userpreference.AuthenticationType as DataAuthenticationType
import com.knocklock.data.source.local.userpreference.UserPreference
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.domain.model.User

fun User.toData() = UserPreference(
    authenticationType = DataAuthenticationType.getValue(authenticationType.name),
    password = password
)

fun UserPreference.toDomain() = User(
    authenticationType = AuthenticationType.getValue(authenticationType.name),
    password = password
)
