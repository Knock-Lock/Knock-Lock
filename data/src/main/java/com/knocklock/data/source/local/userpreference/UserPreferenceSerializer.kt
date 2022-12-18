package com.knocklock.data.source.local.userpreference

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceSerializer @Inject constructor() : Serializer<UserPreference> {

    override val defaultValue = UserPreference(
        authenticationType = AuthenticationType.GESTURE,
        password = "",
        isLockActivated = false
    )

    override suspend fun readFrom(input: InputStream): UserPreference =
        try {
            Json.decodeFromString(
                UserPreference.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPreference", serialization)
        }

    override suspend fun writeTo(t: UserPreference, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(UserPreference.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}