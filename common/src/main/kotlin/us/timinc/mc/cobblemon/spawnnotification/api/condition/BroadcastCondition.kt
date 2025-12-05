package us.timinc.mc.cobblemon.spawnnotification.api.condition

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

interface BroadcastCondition {
    companion object {
        val CODEC: Codec<BroadcastCondition> = BroadcastConditionType.REGISTRY.byNameCodec().dispatch(
            "type",
            BroadcastCondition::getType
        ) {
            @Suppress("UNCHECKED_CAST")
            it.codec as MapCodec<BroadcastCondition>
        }
    }

    fun getType(): BroadcastConditionType<*>
    fun matches(context: BroadcastContext): Boolean
}