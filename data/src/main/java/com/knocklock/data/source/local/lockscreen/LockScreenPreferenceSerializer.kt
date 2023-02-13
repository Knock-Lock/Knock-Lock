package com.knocklock.data.source.local.lockscreen

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
class LockScreenPreferenceSerializer @Inject constructor() : Serializer<LockScreenPreference> {

    override val defaultValue = LockScreenPreference.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LockScreenPreference =
        try {
            Json.decodeFromString(
                LockScreenPreference.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPreference", serialization)
        }

    override suspend fun writeTo(t: LockScreenPreference, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(LockScreenPreference.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}