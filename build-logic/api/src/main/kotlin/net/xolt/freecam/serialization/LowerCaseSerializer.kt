package net.xolt.freecam.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

inline fun <reified T : Enum<T>> lowerCaseEnumSerializer(): KSerializer<T> =
    object : KSerializer<T> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("${T::class.simpleName}.LowerCase", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeString(value.name.lowercase())
        }

        override fun deserialize(decoder: Decoder): T {
            val string = decoder.decodeString()
            return enumValues<T>().first { it.name.equals(string, ignoreCase = true) }
        }
    }
