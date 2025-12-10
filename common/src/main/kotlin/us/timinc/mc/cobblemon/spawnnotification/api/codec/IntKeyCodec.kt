package us.timinc.mc.cobblemon.spawnnotification.api.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

val IntKeyCodec: Codec<Int> = Codec.STRING.comapFlatMap(
    { key ->
        key.toIntOrNull()?.let(DataResult<Int>::success)
            ?: DataResult.error { "Segment index keys must be integers, received '$key'" }
    },
    Int::toString
)